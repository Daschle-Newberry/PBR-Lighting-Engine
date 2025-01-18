package OpenGL_Basic.renderer;

public class Shaders {
    public static Shader mainProgram;
    public static Shader screenProgram;
    public static Shader shadowProgram;
    public static Shader lightSourceProgram;
    public static Shader skyboxProgram;

    public static void loadShaders(){

        mainProgram = new Shader("/assets/shaders/default_vert.glsl","/assets/shaders/pbr_frag.glsl");
        mainProgram.compile();

        screenProgram =  new Shader("/assets/shaders/post_processing_vert.glsl","/assets/shaders/post_processing_frag.glsl");
        screenProgram.compile();

        shadowProgram =  new Shader("/assets/shaders/shadow_map_vert.glsl","/assets/shaders/shadow_map_frag.glsl");
        shadowProgram.compile();

        lightSourceProgram =  new Shader("/assets/shaders/light_vert.glsl","/assets/shaders/light_frag.glsl");
        lightSourceProgram.compile();

        skyboxProgram =  new Shader("/assets/shaders/skybox_vert.glsl","/assets/shaders/skybox_frag.glsl");
        skyboxProgram.compile();
    }
}
