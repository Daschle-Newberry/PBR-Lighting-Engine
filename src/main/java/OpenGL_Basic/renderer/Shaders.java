package OpenGL_Basic.renderer;

public class Shaders {
    public static Shader PBRProgram;
    public static Shader screenProgram;
    public static Shader depthProgram;
    public static Shader shadowProgram;
    public static Shader skyboxProgram;
    public static Shader edgeTestProgram;
    public static Shader debugProgram;
    public static Shader gridProgram;
    public static Shader cubeMappingProgram;
    public static Shader irradianceConvolutionProgram;
    public static Shader specularConvolutionProgram;
    public static Shader precomputeBRDFProgram;
    public static Shader probeProgram;
    public static Shader environmentMappingProgram;

    public static void loadShaders(){

        debugProgram = new Shader("/assets/shaders/debug.vert","/assets/shaders/debug.frag");
        depthProgram = new Shader("/assets/shaders/depth.vert","/assets/shaders/depth.frag");
        skyboxProgram = new Shader("/assets/shaders/skybox.vert","/assets/shaders/skybox.frag");
        PBRProgram = new Shader("/assets/shaders/PBR/pbr.vert", "/assets/shaders/PBR/pbr.frag");
        screenProgram = new Shader("/assets/shaders/screen.vert","/assets/shaders/screen.frag");
        cubeMappingProgram = new Shader("/assets/shaders/MISC/equirectToCube.vert", "/assets/shaders/MISC/equirectToCube.frag");
        irradianceConvolutionProgram = new Shader("/assets/shaders/MISC/equirectToCube.vert", "/assets/shaders/PBR/irradianceConvolution.frag");
        specularConvolutionProgram = new Shader("/assets/shaders/MISC/equirectToCube.vert", "/assets/shaders/PBR/specularConvolution.frag");
        precomputeBRDFProgram = new Shader("/assets/shaders/screen.vert", "/assets/shaders/PBR/IntegrateBRDF.frag");
        probeProgram =  new Shader("/assets/shaders/probe.vert","/assets/shaders/probe.frag");
        environmentMappingProgram = new Shader("/assets/shaders/PBR/reflection.vert","/assets/shaders/PBR/reflection.frag");

    }
}
