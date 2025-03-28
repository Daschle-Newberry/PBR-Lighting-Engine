package PBREngine.engine;

import PBREngine.engine.input.KeyListener;
import PBREngine.engine.input.MouseListener;
import PBREngine.engine.scene.MainScene;
import PBREngine.engine.scene.Scene;
import PBREngine.util.Time;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL32C.GL_TEXTURE_CUBE_MAP_SEAMLESS;
import static org.lwjgl.system.MemoryUtil.*;


public class Window {
    public int width, height;
    private String title;
    private long glfwWindow;

    public float r,g,b,a;
    private boolean fadeToBlack = false;

    private static Window window = null;

    private static Scene currentScene;

    private static double lowestFrameRate = -1;
    public static double totalTime;
    private static int totalFrames;

    private static double totalRenderTime = -1;
    private static double highestRenderTime;

    private Window() {
        this.width = 2560;
        this.height = 1440;
        this.title = "Test";
        r = 0;
        g = 0;
        b = 0;
        a = 1;
    }

    public static void changeScene(int newScene){
        switch (newScene){
            case 0:
                currentScene = new MainScene();
                currentScene.init();

                break;
            default:
                assert false: "Unknown Scene " + newScene + ".";
        }
    }
    public static Window get(){
        if (Window.window == null){
            Window.window = new Window();

        }
        return Window.window;
    }

    public void run(){
        System.out.println("Hello LWJGL" + Version.getVersion());
        init();
        loop();

        //Free Memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }

    public void init(){
        //Error Callback
        GLFWErrorCallback.createPrint(System.err).set();

        //Initialize GLFW
        if (!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        //Configure Window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE,GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE,GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED,GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES,4);


        //Create Window
        glfwWindow = glfwCreateWindow(this.width,this.height,this.title,NULL,NULL);
        //Callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallBack);
        glfwSetMouseButtonCallback(glfwWindow,MouseListener::mouseButtonCallBack);
        glfwSetScrollCallback(glfwWindow,MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallBack);
        glfwSetInputMode(glfwWindow,GLFW_CURSOR,GLFW_CURSOR_DISABLED);


        if (glfwWindow == NULL){
            throw new IllegalStateException("Failed to create GLFW Window");

        }

        //Make OpenGl context current
        glfwMakeContextCurrent(glfwWindow);

        //Enable V-SYNC
        glfwSwapInterval(1);

        //Show Window
        glfwShowWindow(glfwWindow);

       GL.createCapabilities();

       glEnable(GL_DEPTH_TEST);
       glEnable(GL_CULL_FACE);
       glEnable(GL_MULTISAMPLE);
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);

        glfwSetWindowSize(glfwWindow,this.width,this.height);
       glViewport(0,0,width,height);
       glClearColor(.2f,.2f,.2f,1.0f);

        Window.changeScene(0);
    }


    public void loop(){
        double beginTime = Time.getTime();
        double endTime;
        double dt = -1.0f;

        double renderStart;
        double renderEnd;
        while(!glfwWindowShouldClose(glfwWindow)){
            //Poll Events
            glfwPollEvents();

            renderStart = Time.getTime();
            if (dt >= 0){
                currentScene.update(dt);
            }
            renderEnd = Time.getTime();

            double renderTime = (renderEnd-renderStart);

            if(renderTime > highestRenderTime | highestRenderTime < 0){
                highestRenderTime = renderTime;
            }

            totalRenderTime += renderTime;

            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            totalFrames += 1;
            totalTime += dt;
            if(1/dt < lowestFrameRate | lowestFrameRate < 0){
                lowestFrameRate = 1/dt;
            }
            glfwSetWindowTitle(glfwWindow,"FPS " + 1/dt);
            beginTime = endTime;
        }
    }
}
