package OpenGL_Basic.engine;

import OpenGL_Basic.engine.Emitters.DirectionalLight;
import OpenGL_Basic.engine.Emitters.PointLight;
import OpenGL_Basic.engine.input.MouseListener;
import OpenGL_Basic.renderer.Renderer;
import OpenGL_Basic.renderer.buffers.ScreenBuffer;
import OpenGL_Basic.renderer.buffers.DepthBuffer;
import OpenGL_Basic.renderer.Shaders;
import org.joml.Vector3f;

import java.util.ArrayList;

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
    private DepthBuffer shadowMap,edgeMap;
    private boolean renderTris;


    private Renderer renderer;




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

        ArrayList<Model> models =  new ArrayList<>();

        mainModel = new Model("/assets/models/cube.obj",gold);
        mainModel.setScale(.2f);
        mainModel.setPosition(new Vector3f(0.0f,1.0f,0.0f));

        models.add(mainModel);



        room = new Model("/assets/models/room.obj",sand);
        room.setScale(2f);


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
        shadowMap = new DepthBuffer(2560,1440);
        shadowMap.setProjectionOrtho();

        edgeMap = new DepthBuffer(2560,1440);
        Shaders.loadShaders();

        this.renderer = new Renderer(models,pointLights,sun,camera);

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

//    /// Skybox Rendering ///
//    private void skyboxPass(){
//        screenBuffer.bindWrite();
//        glClear(GL_DEPTH_BUFFER_BIT);
//        glDisable(GL_CULL_FACE);
//
//        Shaders.skyboxProgram.use();
//        Shaders.skyboxProgram.uploadMat4f("cameraViewMatrix",camera.getViewMatrixNoTranslation());
//        Shaders.skyboxProgram.uploadMat4f("cameraProjectionMatrix",camera.getProjectionMatrix());
//        skybox.render();
//
//    }
//
//    /// Depth Writing ///
//    private void shadowPass(){
//        shadowMap.bindToWrite();
//
//        glClear(GL_DEPTH_BUFFER_BIT);
//        glEnable(GL_DEPTH_TEST | GL_CULL_FACE);
//
//        Shaders.depthProgram.use();
//
//        Shaders.depthProgram.uploadMat4f("projectionMatrix", shadowMap.getProjectionMatrix());
//        Shaders.depthProgram.uploadMat4f("viewMatrix", sun.getViewMatrix());
//        Shaders.depthProgram.uploadMat4f("modelMatrix", mainModel.getModelMatrix());
//
//        sand.bind();
//        Shaders.mainProgram.uploadMat4f("modelMatrix",mainModel.getModelMatrix());
//        mainModel.render();
//
//
//        Shaders.depthProgram.uploadMat4f("modelMatrix", room.getModelMatrix());
//        room.render();
//
//        Shaders.depthProgram.detach();
//        shadowMap.detach();
//        glBindVertexArray(0);
//    }
//    private void depthPass(){
//        edgeMap.bindToWrite();
//
//        glClear(GL_DEPTH_BUFFER_BIT);
//        glEnable(GL_DEPTH_TEST);
//        glEnable(GL_CULL_FACE);
//
//        Shaders.depthProgram.use();
//
//        Shaders.depthProgram.uploadMat4f("projectionMatrix", camera.getProjectionMatrix());
//        Shaders.depthProgram.uploadMat4f("viewMatrix", camera.getViewMatrix());
//
//        Shaders.depthProgram.uploadMat4f("modelMatrix",mainModel.getModelMatrix());
//        mainModel.render();
//
//
//        Shaders.depthProgram.detach();
//        edgeMap.detach();
//        glBindVertexArray(0);
//    }
//
//    /// Main Pass ///
//
//    private void lightingPass(){
//        screenBuffer.bindWrite();
//        glClear(GL_DEPTH_BUFFER_BIT);
//        glEnable(GL_CULL_FACE | GL_DEPTH_TEST);
//
//        shadowMap.bindToRead();
//
//
//        /// MAIN PASS///
//
//        Shaders.mainProgram.use();
//
//        for(int i = 0; i < MAX_LIGHTS; i++){
//            Shaders.mainProgram.uploadVec3f("lights[" + i +"].color",pointLights[i].getColor());
//            Shaders.mainProgram.uploadFloat("lights[" + i +"].intensity",pointLights[i].getIntensity());
//            Shaders.mainProgram.uploadVec3f("lights[" + i +"].positionDirection",pointLights[i].getPosition());
//            Shaders.mainProgram.uploadInt("lights[" + i +"].isDirectional",0);
//
//        }
//
//        Shaders.mainProgram.uploadMat4f("lightViewMatrix",sun.getViewMatrix());
//        Shaders.mainProgram.uploadMat4f("lightProjectionMatrix",shadowMap.getProjectionMatrix());
//        Shaders.mainProgram.uploadVec3f("sun.direction",sun.getLightFront());
//
//        Shaders.mainProgram.uploadVec3f("cameraPos", camera.getCameraPosition());
//        Shaders.mainProgram.uploadMat4f("cameraViewMatrix",camera.getViewMatrix());
//        Shaders.mainProgram.uploadMat4f("cameraProjectionMatrix",camera.getProjectionMatrix());
//
//
//
//        Shaders.mainProgram.uploadVec2f("shadowMapDimensions",shadowMap.getMapDimensions());
//
//
//        Shaders.mainProgram.uploadInt("albedo",0);
//        Shaders.mainProgram.uploadInt("normalMap",1);
//        Shaders.mainProgram.uploadInt("metallic",2);
//        Shaders.mainProgram.uploadInt("roughness",3);
//        Shaders.mainProgram.uploadInt("AO",4);
//        Shaders.mainProgram.uploadInt("shadowMap",6);
//
//
//        gold.bind();
//        Shaders.mainProgram.uploadMat4f("modelMatrix",mainModel.getModelMatrix());
//        mainModel.render();
//
//        Shaders.mainProgram.uploadMat4f("modelMatrix",room.getModelMatrix());
//        sand.bind();
//        room.render();
//
//
//
//        Shaders.mainProgram.detach();
//        glBindVertexArray(0);
//
//    }
//
//    /// Post Passes ///
//    private void edgePass(){
//        screenBuffer.bindWrite();
//        glClear(GL_DEPTH_BUFFER_BIT);
//        glEnable(GL_CULL_FACE | GL_DEPTH_TEST);
//
//        edgeMap.bindToRead();
//
//        Shaders.edgeTestProgram.use();
//
//        Shaders.edgeTestProgram.uploadMat4f("cameraProjectionMatrix", camera.getProjectionMatrix());
//        Shaders.edgeTestProgram.uploadMat4f("cameraViewMatrix", camera.getViewMatrix());
//
//        Shaders.edgeTestProgram.uploadInt("depthMap",6);
//        Shaders.edgeTestProgram.uploadVec2f("depthMapDimensions",edgeMap.getMapDimensions());
//        Shaders.edgeTestProgram.uploadMat4f("modelMatrix",mainModel.getModelMatrix());
//        mainModel.render();
//
//
//        Shaders.edgeTestProgram.detach();
//        glBindVertexArray(0);
//    }

    @Override
    public void render() {
//        skyboxPass();
//
//        depthPass();
//        shadowPass();
//
//        lightingPass();
//        edgeMap.bindToRead();
//        screenBuffer.render();

        renderer.start();

    }
}
