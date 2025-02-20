package OpenGL_Basic.engine;

import OpenGL_Basic.engine.Emitters.DirectionalLight;
import OpenGL_Basic.engine.Emitters.PointLight;
import OpenGL_Basic.engine.input.MouseListener;
import OpenGL_Basic.engine.postprocessing.ScreenBuffer;
import OpenGL_Basic.engine.postprocessing.DepthMap;
import OpenGL_Basic.renderer.Shaders;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL30.*;

public class GameScene extends Scene{
    //Scene Elements
    private int MAX_LIGHTS;
    private PointLight pointLights[];
    private DirectionalLight sun;

    private Model mainModel,room,orb;
    private Material rustedMetal,gold,sand,obsidian;
    private Camera camera;

    private Skybox skybox;

    //Gameplay Elements
    private Player player;

    //Rendering Elements
    private ScreenBuffer screenBuffer;
    private DepthMap shadowMap,edgeMap;
    private boolean renderTris;


    //Misc
    private int ticks;
    private int distance = 8;


    @Override
    public void init() {
        //Scene Elements
        MAX_LIGHTS = 4;
        pointLights = new PointLight[MAX_LIGHTS];

        pointLights[0] = new PointLight(new Vector3f(-1.0f,-1.0f,1.0f),2.0f,new Vector3f(1.0f,1.0f,1.0f));
        pointLights[1] = new PointLight(new Vector3f(1.0f,-1.0f,1.0f),2.0f,new Vector3f(1.0f,1.0f,1.0f));
        pointLights[2] = new PointLight(new Vector3f(-1.0f,1.0f,1.0f),2.0f,new Vector3f(1.0f,1.0f,1.0f));
        pointLights[3] = new PointLight(new Vector3f(1.0f,1.0f,1.0f),2.0f,new Vector3f(1.0f,1.0f,1.0f));

        sun =  new DirectionalLight(new Vector3f(0.0f,.4f,1.0f), new Vector3f(0.0f,0.0f,1.0f));

        mainModel = new Model("/assets/models/cube.obj");
        mainModel.setScale(.2f);



        room = new Model("/assets/models/room.obj");
        room.setScale(2f);

        orb =  new Model("/assets/models/sphere.obj");
        orb.setScale(.2f);

        rustedMetal = new Material(
                "/assets/materials/rusted_iron/albedo.png",
                "/assets/materials/rusted_iron/normal.png",
                "/assets/materials/rusted_iron/metallic.png",
                "/assets/materials/rusted_iron/roughness.png",
                "/assets/materials/rusted_iron/ao.png");

        gold = new Material(
                "/assets/materials/gold/albedo.png",
                "/assets/materials/gold/normal.png",
                "/assets/materials/gold/metallic.png",
                "/assets/materials/gold/roughness.png",
                "/assets/materials/gold/ao.png"
        );

        sand =  new Material(
                "/assets/materials/sand/sand-dunes1_albedo.png",
                "/assets/materials/sand/sand-dunes1_normal-ogl.png",
                "/assets/materials/sand/sand-dunes1_metallic.png",
                "/assets/materials/sand/sand-dunes1_roughness.png",
                "/assets/materials/sand/sand-dunes1_ao.png"
        );
//
//        woodenWall = new Material(
//                "/assets/materials/pattern_wooden_wall/patterned_wooden_wall_panel_48_28_diffuse.jpg",
//                "/assets/materials/pattern_wooden_wall/patterned_wooden_wall_panel_48_28_normal_opengl.jpg",
//                "/assets/materials/pattern_wooden_wall/patterned_wooden_wall_panel_48_28_metallic.jpg",
//                "/assets/materials/pattern_wooden_wall/patterned_wooden_wall_panel_48_28_roughness.jpg",
//                "/assets/materials/pattern_wooden_wall/patterned_wooden_wall_panel_48_28_ao.jpg"
//            );

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
        shadowMap = new DepthMap(2560,1440);
        shadowMap.setProjectionOrtho();

        edgeMap = new DepthMap(2560,1440);
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
        player.updatePlayer();
        render();
    }


    private void shadowPass(){
        shadowMap.bindToWrite();
        Shaders.shadowProgram.use();
        glClear(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);


        Shaders.shadowProgram.uploadMat4f("projectionMatrix", shadowMap.getProjectionMatrix());
        Shaders.shadowProgram.uploadMat4f("viewMatrix", sun.getViewMatrix());
        Shaders.shadowProgram.uploadMat4f("modelMatrix", mainModel.getModelMatrix());

        for (int x = -3; x < 1; x++){
            for (int y = 1; y < 3; y++){
                mainModel.setPosition(new Vector3f(x*3,y*3,0f));
                Shaders.shadowProgram.uploadMat4f("modelMatrix",mainModel.getModelMatrix());
                mainModel.render();

            }
        }

        mainModel.render();

        Shaders.shadowProgram.uploadMat4f("modelMatrix", room.getModelMatrix());

        room.render();

        Shaders.shadowProgram.detach();
        glBindVertexArray(0);
    }

    private void lightingPass(){
        screenBuffer.bind();
        shadowMap.bindToRead();
        glClear(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glCullFace(GL_BACK);

        glClearColor(.2f,.2f,.2f,1f);

        glViewport(0,0,Window.get().width,Window.get().height);


        /// MAIN PASS///

        Shaders.mainProgram.use();

        for(int i = 0; i < MAX_LIGHTS; i++){
            Shaders.mainProgram.uploadVec3f("lights[" + i +"].color",pointLights[i].getColor());
            Shaders.mainProgram.uploadFloat("lights[" + i +"].intensity",pointLights[i].getIntensity());
            Shaders.mainProgram.uploadVec3f("lights[" + i +"].positionDirection",pointLights[i].getPosition());
            Shaders.mainProgram.uploadInt("lights[" + i +"].isDirectional",0);

        }


        Shaders.mainProgram.uploadVec3f("cameraPos", camera.getCameraPosition());

        Shaders.mainProgram.uploadMat4f("cameraViewMatrix",camera.getViewMatrix());
        Shaders.mainProgram.uploadMat4f("cameraProjectionMatrix",camera.getProjectionMatrix());

        Shaders.mainProgram.uploadMat4f("lightViewMatrix",sun.getViewMatrix());
        Shaders.mainProgram.uploadMat4f("lightProjectionMatrix",shadowMap.getProjectionMatrix());
        Shaders.mainProgram.uploadVec3f("sun.direction",sun.getLightFront());

        Shaders.mainProgram.uploadVec2f("shadowMapDimensions",shadowMap.getMapDimensions());
        Shaders.mainProgram.uploadInt("shadowMap",6);


        Shaders.mainProgram.uploadInt("albedo",0);
        Shaders.mainProgram.uploadInt("normalMap",1);
        Shaders.mainProgram.uploadInt("metallic",2);
        Shaders.mainProgram.uploadInt("roughness",3);
        Shaders.mainProgram.uploadInt("AO",4);


        for (int x = -3; x < 1; x++){
            for (int y = 1; y < 3; y++){
                mainModel.setPosition(new Vector3f(x*3,y*3,0f));
                Shaders.mainProgram.uploadMat4f("modelMatrix",mainModel.getModelMatrix());
                if (x == -3){
                    rustedMetal.bind();
                }
                else if (x == -2){
                    rustedMetal.bind();
                }
                else if (x == -1){
                    sand.bind();
                }
                else if(x == 0){
                    sand.bind();
                }
                mainModel.render();

            }
        }


        Shaders.mainProgram.uploadMat4f("modelMatrix",room.getModelMatrix());
        sand.bind();
        room.render();



        Shaders.mainProgram.detach();
        glBindVertexArray(0);

    }

    private void skyboxPass(){
        screenBuffer.bind();
        glClear(GL_COLOR_BUFFER_BIT);
        glDisable(GL_CULL_FACE);
        glDepthMask(false);

        Shaders.skyboxProgram.use();
        Shaders.skyboxProgram.uploadMat4f("cameraViewMatrix",camera.getViewMatrixNoTranslation());
        Shaders.skyboxProgram.uploadMat4f("cameraProjectionMatrix",camera.getProjectionMatrix());
        skybox.render();
        glDepthMask(true);

    }

    private void depthPass(){
        screenBuffer.bind();
        Shaders.edgeTestProgram.use();
        glClear(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);


        Shaders.edgeTestProgram.uploadMat4f("cameraProjectionMatrix", camera.getProjectionMatrix());
        Shaders.edgeTestProgram.uploadMat4f("cameraViewMatrix", camera.getViewMatrix());


        for (int x = -3; x < 1; x++){
            for (int y = 1; y < 3; y++){
                mainModel.setPosition(new Vector3f(x*3,y*3,0f));
                Shaders.edgeTestProgram.uploadMat4f("modelMatrix",mainModel.getModelMatrix());
                mainModel.render();

            }
        }

        mainModel.render();

        Shaders.edgeTestProgram.uploadMat4f("modelMatrix", room.getModelMatrix());

        room.render();

        Shaders.edgeTestProgram.detach();
        glBindVertexArray(0);

    }
    private void postProcessingPass(){

        screenBuffer.render();
    }
    @Override
    public void render() {
        skyboxPass();
        depthPass();
        screenBuffer.render();
    }
}
