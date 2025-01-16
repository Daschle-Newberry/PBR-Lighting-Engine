package OpenGL_Basic.engine;

import OpenGL_Basic.engine.input.KeyListener;
import OpenGL_Basic.engine.input.MouseListener;
import OpenGL_Basic.engine.postprocessing.ScreenBuffer;
import OpenGL_Basic.engine.postprocessing.ShadowMap;
import OpenGL_Basic.renderer.Shaders;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL30.*;

public class GameScene extends Scene{
    private DirectionalLight sun;
    private int ticks;
    private int distance;

    private Model cube;
    private Model room;
    private Model orb;

    private Material stone;

    private Camera camera;
    private ScreenBuffer screenBuffer;
    private ShadowMap shadowMap;

    private int cullMode;
    private boolean renderTris;
    private Player player;
    @Override
    public void init() {
        Shaders.loadShaders();
        camera = new Camera(new Vector3f(0.0f,0.0f,0.0f),1);

        cube = new Model("/assets/models/cube.obj");
        stone = new Material("/assets/textures/container.jpg");
        cube.setScale(.2f);
        cube.setPosition(new Vector3f(0,1.001f,0));

        room = new Model("/assets/models/room.obj");
        room.setScale(2f);


        orb =  new Model("/assets/models/sphere.obj");
        orb.setScale(.2f);

        sun = new DirectionalLight(new Vector3f(0,3.371f,3.369f),new Vector3f(0,.06743f,.7385f));

        screenBuffer = new ScreenBuffer();
        shadowMap = new ShadowMap(2560,1440);
        shadowMap.setShadowMapProjectionOrtho();

        player = new Player(new Vector3f(0,0,0),camera);

    }

    @Override
    public void framebufferSizeCallback(long window, int width, int height) {
        screenBuffer.framebufferSizeCallback(window,width,height);
    }

    @Override
    public void update(double dt) {
        camera.processMouseMovement(MouseListener.getDx(),MouseListener.getDy());
        MouseListener.proccessMovement();


//        if (KeyListener.get().isKeyPressed(265)){
//           ticks++;
//           tickSun();
//        }
//        else if(KeyListener.get().isKeyPressed(264)){
//           ticks--;
//           tickSun();
//        }
//        else if(KeyListener.get().isKeyPressed(263)){
//            if (distance > 0){
//                distance --;
//                tickSun();
//            }
//        }
//        else if(KeyListener.get().isKeyPressed(262)){
//            distance ++;
//            tickSun();
//
//        }
//
//        else if(KeyListener.get().isKeyPressed(32)){
//            System.out.println("Front " + sun.getLightFront() + " Position " + sun.getPosition());
//
//        }
        orb.setPosition(sun.getPosition());
        player.updatePlayer();


        if (KeyListener.get().isKeyPressed(49)){
          cullMode = 0;
        }
        else if(KeyListener.get().isKeyPressed(50)){
         cullMode = 1;
        }
        else if(KeyListener.get().isKeyPressed(51)){
          cullMode = 2;
        }
        render();
    }

    private void tickSun(){
        sun.setLightPos(new Vector3f(0,distance *(float) Math.sin(ticks/50f),distance *(float) Math.cos(ticks/50f)));
        sun.setLightFront(new Vector3f(0,(float) Math.sin(ticks/50f),(float) Math.cos(ticks/50f)));
    }

    private void shadowPass(){
        shadowMap.bindToWrite();
        Shaders.shadowProgram.use();

        glDisable(GL_CULL_FACE);

        glEnable(GL_DEPTH_TEST);

        glClear(GL_DEPTH_BUFFER_BIT);


        Shaders.shadowProgram.uploadMat4f("projectionMatrix", shadowMap.getProjectionMatrix());
        Shaders.shadowProgram.uploadMat4f("viewMatrix", sun.getViewMatrix());
        Shaders.shadowProgram.uploadMat4f("modelMatrix", cube.getModelMatrix());

        cube.render();

        Shaders.shadowProgram.uploadMat4f("modelMatrix", room.getModelMatrix());

        room.render();

        Shaders.shadowProgram.detach();
        glBindVertexArray(0);


    }

    private void lightingPass(){
        screenBuffer.bind();
        shadowMap.bindToRead();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);


        glClearColor(0f,0f,0f,1.0f);
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);

        glViewport(0,0,Window.get().width,Window.get().height);


        glDisable(GL_CULL_FACE);
        Shaders.lightSourceProgram.use();

        Shaders.lightSourceProgram.uploadMat4f("cameraViewMatrix",camera.getViewMatrix());
        Shaders.lightSourceProgram.uploadMat4f("cameraProjectionMatrix",camera.getProjectionMatrix());
        Shaders.lightSourceProgram.uploadMat4f("modelMatrix",orb.getModelMatrix());

        orb.render();

        Shaders.mainProgram.use();


        Shaders.mainProgram.uploadVec3f("sunDirection",sun.getLightFront());
        Shaders.mainProgram.uploadMat4f("cameraViewMatrix",camera.getViewMatrix());
        Shaders.mainProgram.uploadMat4f("cameraProjectionMatrix",camera.getProjectionMatrix());

        Shaders.mainProgram.uploadMat4f("lightViewMatrix",sun.getViewMatrix());
        Shaders.mainProgram.uploadMat4f("lightProjectionMatrix",shadowMap.getProjectionMatrix());

        Shaders.mainProgram.uploadInt("textureIMG",0);
        Shaders.mainProgram.uploadInt("shadowMap",1);

        Shaders.mainProgram.uploadMat4f("modelMatrix",cube.getModelMatrix());
        Shaders.mainProgram.uploadInt("isTextured",1);
        stone.bindTexture();
        cube.render();

        Shaders.mainProgram.uploadMat4f("modelMatrix",room.getModelMatrix());
        Shaders.mainProgram.uploadInt("isTextured",0);
        room.render();

        Shaders.mainProgram.detach();
        glBindVertexArray(0);
        //Render to screen
        screenBuffer.render();
    }
    @Override
    public void render() {
        shadowPass();
        lightingPass();

    }
}
