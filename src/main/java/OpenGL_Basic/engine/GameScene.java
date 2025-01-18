package OpenGL_Basic.engine;

import OpenGL_Basic.engine.input.KeyListener;
import OpenGL_Basic.engine.input.MouseListener;
import OpenGL_Basic.engine.postprocessing.ScreenBuffer;
import OpenGL_Basic.engine.postprocessing.ShadowMap;
import OpenGL_Basic.renderer.Shader;
import OpenGL_Basic.renderer.Shaders;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL30.*;

public class GameScene extends Scene{
    //Scene Elements
    private DirectionalLight sun;
    private Model dragon,room,orb;
    private Material metal,marble;
    private Camera camera;

    private Skybox skybox;

    //Gameplay Elements
    private Player player;


    //Rendering Elements
    private ScreenBuffer screenBuffer;
    private ShadowMap shadowMap;
    private boolean renderTris;


    //Misc
    private int ticks;
    private int distance = 3;


    @Override
    public void init() {
        //Scene Elements
        sun = new DirectionalLight(new Vector3f(0,0,distance),new Vector3f(0,0,1));

        dragon = new Model("/assets/models/sphere.obj");
        dragon.setScale(.2f);
        dragon.setPosition(new Vector3f(0,0.2f,0));
        dragon.setRotation(20,new Vector3f(0,1,0));

        room = new Model("/assets/models/room.obj");
        room.setScale(2f);

        orb =  new Model("/assets/models/sphere.obj");
        orb.setScale(.2f);
        metal = new Material(
                "/assets/materials/rusted_iron/albedo.png","/assets/materials/rusted_iron/normal.png",
                "/assets/materials/rusted_iron/metallic.png","/assets/materials/rusted_iron/roughness.png",
                "/assets/materials/rusted_iron/ao.png");

        marble = new Material(
                "/assets/materials/marble/marble-speckled-albedo.png",
                "/assets/materials/marble/marble-speckled-normal.png",
                "/assets/materials/marble/marble-speckled-metalness.png",
                "/assets/materials/marble/marble-speckled-roughness.png",
                null
        );

        camera = new Camera(new Vector3f(0.0f,0.0f,0.0f),1);

        skybox =  new Skybox(new String[]{
                        "/assets/skybox/right.jpg",
                        "/assets/skybox/left.jpg",
                        "/assets/skybox/top.jpg",
                        "/assets/skybox/bottom.jpg",
                        "/assets/skybox/front.jpg",
                        "/assets/skybox/back.jpg"});

        //Gameplay Elements
        player = new Player(new Vector3f(0,0,0),camera);

        //Render Elements
        screenBuffer = new ScreenBuffer();
        shadowMap = new ShadowMap(2560,1440);
        shadowMap.setShadowMapProjectionOrtho();
        Shaders.loadShaders();

    }

    @Override
    public void framebufferSizeCallback(long window, int width, int height) {
        screenBuffer.framebufferSizeCallback(window,width,height);
    }

    @Override
    public void update(double dt) {
        camera.processMouseMovement(MouseListener.getDx(),MouseListener.getDy());
        MouseListener.proccessMovement();


        if (KeyListener.get().isKeyPressed(265)){
           ticks++;
           tickSun();
        }
        else if(KeyListener.get().isKeyPressed(264)){
           ticks--;
           tickSun();
        }
        else if(KeyListener.get().isKeyPressed(263)){
            if (distance > 3){
                distance --;
                tickSun();
            }
        }
        else if(KeyListener.get().isKeyPressed(262)){
            distance ++;
            tickSun();

        }

        orb.setPosition(sun.getPosition());
        player.updatePlayer();



        render();
    }

    private void tickSun(){
        sun.setLightPos(new Vector3f(0,distance *(float) Math.sin(ticks/50f),distance *(float) Math.cos(ticks/50f)));
        sun.setLightFront(new Vector3f(0,(float) Math.sin(ticks/50f),(float) Math.cos(ticks/50f)));
    }

    private void shadowPass(){
        shadowMap.bindToWrite();
        Shaders.shadowProgram.use();
        glEnable(GL_DEPTH_TEST);
        glClear(GL_DEPTH_BUFFER_BIT);


        Shaders.shadowProgram.uploadMat4f("projectionMatrix", shadowMap.getProjectionMatrix());
        Shaders.shadowProgram.uploadMat4f("viewMatrix", sun.getViewMatrix());
        Shaders.shadowProgram.uploadMat4f("modelMatrix", dragon.getModelMatrix());

        dragon.render();

        Shaders.shadowProgram.uploadMat4f("modelMatrix", room.getModelMatrix());

        room.render();

        Shaders.shadowProgram.detach();
        glBindVertexArray(0);


    }

    private void lightingPass(){
        screenBuffer.bind();
        shadowMap.bindToRead();

        glEnable(GL_DEPTH_TEST|GL_CULL_FACE);
        glDepthFunc(GL_LESS);
        glCullFace(GL_BACK);


        glClearColor(.2f,.2f,.2f,1f);
        glClear(GL_DEPTH_BUFFER_BIT);

        glViewport(0,0,Window.get().width,Window.get().height);


        Shaders.lightSourceProgram.use();

        Shaders.lightSourceProgram.uploadMat4f("cameraViewMatrix",camera.getViewMatrix());
        Shaders.lightSourceProgram.uploadMat4f("cameraProjectionMatrix",camera.getProjectionMatrix());
        Shaders.lightSourceProgram.uploadMat4f("modelMatrix",orb.getModelMatrix());
        orb.render();

        /// MAIN PASS///

        Shaders.mainProgram.use();


        Shaders.mainProgram.uploadVec3f("sun.color",new Vector3f(1.0f));
        Shaders.mainProgram.uploadVec3f("sun.intensity",new Vector3f(1f));
        Shaders.mainProgram.uploadVec3f("sun.direction",sun.getLightFront());


        Shaders.mainProgram.uploadVec3f("cameraPos", camera.getCameraPosition());

        Shaders.mainProgram.uploadMat4f("cameraViewMatrix",camera.getViewMatrix());
        Shaders.mainProgram.uploadMat4f("cameraProjectionMatrix",camera.getProjectionMatrix());

        Shaders.mainProgram.uploadMat4f("lightViewMatrix",sun.getViewMatrix());
        Shaders.mainProgram.uploadMat4f("lightProjectionMatrix",shadowMap.getProjectionMatrix());

        Shaders.mainProgram.uploadVec2f("shadowMapDimensions",shadowMap.getMapDimensions());
        Shaders.mainProgram.uploadInt("shadowMap",1);





        Shaders.mainProgram.uploadMat4f("modelMatrix",dragon.getModelMatrix());
        Shaders.mainProgram.uploadInt("isTextured",1);
        Shaders.mainProgram.uploadInt("textureIMG",0);
        marble.bind();

        Shaders.mainProgram.uploadInt("albedo",0);
        Shaders.mainProgram.uploadInt("normal",1);
        Shaders.mainProgram.uploadInt("metallic",2);
        Shaders.mainProgram.uploadInt("roughness",3);
        Shaders.mainProgram.uploadInt("AO",4);


        dragon.render();

        Shaders.mainProgram.uploadMat4f("modelMatrix",room.getModelMatrix());
        Shaders.mainProgram.uploadInt("isTextured",0);

        Shaders.mainProgram.uploadFloat("material.roughness",1.0f);
        Shaders.mainProgram.uploadVec3f("material.color",new Vector3f(0.0f,.4f,.4f));
        Shaders.mainProgram.uploadInt("material.metallic",0);
//        room.render();

        Shaders.mainProgram.detach();
        glBindVertexArray(0);

    }

    private void skyboxPass(){
        screenBuffer.bind();
        glDepthMask(false);
        glDisable(GL_CULL_FACE);

        Shaders.skyboxProgram.use();
        Shaders.skyboxProgram.uploadMat4f("cameraViewMatrix",camera.getViewMatrixNoTranslation());
        Shaders.skyboxProgram.uploadMat4f("cameraProjectionMatrix",camera.getProjectionMatrix());
        skybox.render();
        glDepthMask(true);

    }
    @Override
    public void render() {
        skyboxPass();
        shadowPass();
        lightingPass();
        screenBuffer.render();
    }
}
