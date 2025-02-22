package OpenGL_Basic.renderer;

public class Shaders {
    public static Shader mainProgram;
    public static Shader screenProgram;
    public static Shader depthProgram;
    public static Shader skyboxProgram;
    public static Shader edgeTestProgram;

    public static void loadShaders(){

        mainProgram = new Shader("/assets/shaders/default.vert", "/assets/shaders/pbr.frag");
        mainProgram.compile();

        screenProgram =  new Shader("/assets/shaders/screen.vert", "/assets/shaders/screen.frag");
        screenProgram.compile();

        depthProgram =  new Shader("/assets/shaders/depth.vert", "/assets/shaders/depth.frag");
        depthProgram.compile();

        skyboxProgram =  new Shader("/assets/shaders/skybox.vert", "/assets/shaders/skybox.frag");
        skyboxProgram.compile();

        edgeTestProgram = new Shader("/assets/shaders/screen.vert", "/assets/shaders/edge.frag");
        edgeTestProgram.compile();
    }
}
