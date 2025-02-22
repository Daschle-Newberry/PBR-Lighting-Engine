package OpenGL_Basic.renderer.passes;

import OpenGL_Basic.engine.Camera;
import OpenGL_Basic.engine.Emitters.DirectionalLight;
import OpenGL_Basic.engine.Model;
import OpenGL_Basic.engine.Perspective;
import OpenGL_Basic.renderer.RenderPass;
import OpenGL_Basic.renderer.Renderer;
import OpenGL_Basic.renderer.Shaders;
import OpenGL_Basic.renderer.buffers.DepthBuffer;


import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class DepthPass extends RenderPass {
    private Renderer renderer;
    private DepthBuffer outputBuffer;
    private Perspective perspective;

    public DepthPass(Renderer renderer,Perspective perspective, DepthBuffer outputBuffer){
        this.renderer = renderer;
        this.outputBuffer = outputBuffer;
        this.perspective = perspective;
    }


    @Override
    public void render() {
        DirectionalLight sceneLight = renderer.sceneLight;
        ArrayList<Model> models = renderer.renderQueue;

        outputBuffer.bindToWrite();

        glClear(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);


        Shaders.depthProgram.use();

        Shaders.depthProgram.uploadMat4f("projectionMatrix", perspective.getProjectionMatrix());
        Shaders.depthProgram.uploadMat4f("viewMatrix", perspective.getViewMatrix());

        for(Model model : models){
            Shaders.depthProgram.uploadMat4f("modelMatrix",model.getModelMatrix());
            model.render();
        }


        Shaders.depthProgram.detach();
        outputBuffer.detach();
        glBindVertexArray(0);
    }
}
