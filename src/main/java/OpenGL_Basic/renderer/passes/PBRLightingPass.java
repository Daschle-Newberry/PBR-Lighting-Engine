//package OpenGL_Basic.renderer.passes;
//
//import OpenGL_Basic.engine.Camera;
//import OpenGL_Basic.engine.Emitters.DirectionalLight;
//import OpenGL_Basic.engine.Emitters.PointLight;
//import OpenGL_Basic.engine.Model;
//import OpenGL_Basic.renderer.Renderer;
//import OpenGL_Basic.renderer.Shaders;
//import OpenGL_Basic.renderer.buffers.ColorBuffer;
//import OpenGL_Basic.renderer.buffers.DepthBuffer;
//import OpenGL_Basic.renderer.buffers.OutputBuffer;
//import org.lwjgl.BufferUtils;
//
//import java.nio.ByteBuffer;
//import java.nio.IntBuffer;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//
//import static OpenGL_Basic.renderer.Renderer.R_OUTPUT_BUFFER;
//import static OpenGL_Basic.renderer.Renderer.R_SCENELIGHT_DEPTH_BUFFER;
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
//
//public class PBRLightingPass implements RenderPass {
//    public static int[] dependencies = new int[]{R_SCENELIGHT_DEPTH_BUFFER};
//    private int renderShadows = 1;
//    private Renderer renderer;
//    private OutputBuffer outputBuffer;
//    private DepthBuffer shadowMap;
//
//    public PBRLightingPass(Renderer renderer){
//        this.renderer = renderer;
//        this.outputBuffer = renderer.outputs.get(R_OUTPUT_BUFFER);
//
//        IntBuffer totalUniforms = BufferUtils.createIntBuffer(1);
//        glGetProgramiv(Shaders.mainProgram.getID(),GL_ACTIVE_UNIFORMS, totalUniforms);
//
//        for(int i = 0; i < totalUniforms.get(0); i++) {
//            IntBuffer length = BufferUtils.createIntBuffer(1);
//            IntBuffer size = BufferUtils.createIntBuffer(1);
//            IntBuffer type = BufferUtils.createIntBuffer(1);
//            ByteBuffer name = BufferUtils.createByteBuffer(100);
//            glGetActiveUniform(Shaders.mainProgram.getID(),i,length,size,type,name);
//
//            String s = StandardCharsets.UTF_8.decode(name.slice(0,length.get(0))).toString();
//            System.out.println(s);
//
//        }
//        System.out.println(totalUniforms.get(0));
//    }
//    @Override
//    public void render() {
//        outputBuffer.bindToWrite();
//        glClear(GL_DEPTH_BUFFER_BIT);
//        glEnable(GL_DEPTH_TEST);
//        glEnable(GL_CULL_FACE);
//
//        shadowMap.bindToRead();
//
//        ArrayList<Model> sceneObjects = renderer.models;
//        PointLight[] pointLights = renderer.pointLights;
//        DirectionalLight sceneLight = renderer.sceneLight;
//        Camera sceneCamera = renderer.sceneCamera;
//
//        /// MAIN PASS///
//
//        Shaders.mainProgram.use();
//
//        for(int i = 0; i < pointLights.length; i++){
//            Shaders.mainProgram.uploadVec3f("lights[" + i +"].color",pointLights[i].getColor());
//            Shaders.mainProgram.uploadFloat("lights[" + i +"].intensity",pointLights[i].getIntensity());
//            Shaders.mainProgram.uploadVec3f("lights[" + i +"].positionDirection",pointLights[i].getPosition());
//            Shaders.mainProgram.uploadInt("lights[" + i +"].isDirectional",0);
//
//        }
//
//        Shaders.mainProgram.uploadMat4f("lightViewMatrix",sceneLight.getViewMatrix());
//        Shaders.mainProgram.uploadMat4f("lightProjectionMatrix",sceneLight.getProjectionMatrix());
//        Shaders.mainProgram.uploadVec3f("sun.direction",sceneLight.getLightFront());
//
//        Shaders.mainProgram.uploadVec3f("cameraPos", sceneCamera.getCameraPosition());
//        Shaders.mainProgram.uploadMat4f("cameraViewMatrix",sceneCamera.getViewMatrix());
//        Shaders.mainProgram.uploadMat4f("cameraProjectionMatrix",sceneCamera.getProjectionMatrix());
//
//
//        Shaders.mainProgram.uploadInt("renderShadows",renderShadows);
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
//        for(Model model : sceneObjects){
//            model.bindMaterial();
//            Shaders.mainProgram.uploadMat4f("modelMatrix",model.getModelMatrix());
//            model.render();
//        }
//
//
//        Shaders.mainProgram.detach();
//        outputBuffer.detach();
//        shadowMap.detach();
//        glBindVertexArray(0);
//    }
//    @Override
//    public OutputBuffer getBuffer() {return this.outputBuffer;}
//
//    public int[] getDependencies() {
//        return dependencies;
//    }
//
//    @Override
//    public void sourceDependencies() {
//        this.shadowMap = (DepthBuffer) renderer.outputs.get(R_SCENELIGHT_DEPTH_BUFFER);
//    }
//
//
//}
