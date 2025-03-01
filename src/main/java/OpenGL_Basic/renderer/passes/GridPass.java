//package OpenGL_Basic.renderer.passes;
//
//import OpenGL_Basic.engine.Camera;
//import OpenGL_Basic.renderer.Renderer;
//import OpenGL_Basic.renderer.Shaders;
//import OpenGL_Basic.renderer.buffers.OutputBuffer;
//
//import static OpenGL_Basic.renderer.Renderer.R_OUTPUT_BUFFER;
//import static org.lwjgl.opengl.GL11.*;
//
//public class GridPass implements  RenderPass {
//    private static int[] dependencies = null;
//    private Renderer renderer;
//    private OutputBuffer outputBuffer;
//    public GridPass(Renderer renderer){
//        this.renderer = renderer;
//        this.outputBuffer = renderer.outputs.get(R_OUTPUT_BUFFER);
//    }
//    @Override
//    public void render(){
//        outputBuffer.bindToWrite();
//        glDisable(GL_CULL_FACE);
//        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
//
//        Camera sceneCamera = renderer.sceneCamera;
//
//        Shaders.gridProgram.use();
//
//        Shaders.gridProgram.uploadMat4f("viewMatrix",sceneCamera.getViewMatrix());
//        Shaders.gridProgram.uploadMat4f("projectionMatrix", sceneCamera.getProjectionMatrix());
//        Shaders.gridProgram.uploadVec3f("cameraPosition", sceneCamera.getCameraPosition());
//
//
//        glDrawArrays(GL_TRIANGLES,0,6);
//
//
//        outputBuffer.detach();
//        Shaders.gridProgram.detach();
//    }
//
//    @Override
//    public void sourceDependencies() {
//
//    }
//
//    @Override
//    public int[] getDependencies() {
//        return new int[0];
//    }
//
//    @Override
//    public OutputBuffer getBuffer() {
//        return null;
//    }
//}
