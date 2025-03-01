//package OpenGL_Basic.renderer.passes;
//
//import OpenGL_Basic.engine.Model;
//import OpenGL_Basic.engine.Perspective;
//import OpenGL_Basic.renderer.Renderer;
//import OpenGL_Basic.renderer.Shaders;
//import OpenGL_Basic.renderer.buffers.DepthBuffer;
//import OpenGL_Basic.renderer.buffers.OutputBuffer;
//
//
//import java.util.ArrayList;
//
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.glBindVertexArray;
//
//public class DepthPass implements RenderPass {
//    private static int[] dependencies = null;
//
//    private Renderer renderer;
//    private Perspective perspective;
//    private DepthBuffer outputBuffer;
//
//
//    public DepthPass(Renderer renderer,Perspective perspective){
//        this.renderer = renderer;
//        this.outputBuffer = new DepthBuffer(2560,1440);
//        this.perspective = perspective;
//    }
//
//
//    @Override
//    public void render() {
//        ArrayList<Model> models = renderer.models;
//
//        outputBuffer.bindToWrite();
//
//        glClear(GL_DEPTH_BUFFER_BIT);
//        glEnable(GL_DEPTH_TEST);
//        glEnable(GL_CULL_FACE);
//
//
//        Shaders.depthProgram.use();
//
//        Shaders.depthProgram.uploadMat4f("projectionMatrix", perspective.getProjectionMatrix());
//        Shaders.depthProgram.uploadMat4f("viewMatrix", perspective.getViewMatrix());
//
//        for(Model model : models){
//            Shaders.depthProgram.uploadMat4f("modelMatrix",model.getModelMatrix());
//            model.render();
//        }
//
//
//        Shaders.depthProgram.detach();
//        outputBuffer.detach();
//        glBindVertexArray(0);
//    }
//
//    @Override
//    public OutputBuffer getBuffer() {return this.outputBuffer;}
//
//    public int[] getDependencies() {
//        return dependencies;
//    }
//
//    @Override
//    public void sourceDependencies() {
//
//    }
//
//}
