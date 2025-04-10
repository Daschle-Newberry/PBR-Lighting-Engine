package PBREngine.renderer.passes;

import PBREngine.engine.Window;
import PBREngine.engine.scene.elements.Camera;
import PBREngine.engine.scene.elements.model.Model;
import PBREngine.engine.scene.SceneData;
import PBREngine.renderer.Renderer;
import PBREngine.renderer.Shader;
import PBREngine.renderer.Shaders;
import PBREngine.renderer.buffers.FrameBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class DepthPass extends RenderPass {
    private static Shader shader = Shaders.depthProgram;
    private SceneData sceneData;
    private FrameBuffer FBO;
    private Camera perspective;
    private int depthBufferRequest;

    public DepthPass(Renderer renderer, SceneData sceneData, Camera perspective, int depthBufferRequest) {
        if(!shader.isCompiled) shader.compile();
        if(!Shaders.probeProgram.isCompiled) Shaders.probeProgram.compile();
        this.depthBufferRequest = depthBufferRequest;
        this.sceneData = sceneData;
        FBO = createFrameBuffer(null,depthBufferRequest,renderer, Window.get().width, Window.get().height);
        this.perspective = perspective;
    }

    @Override
    public void resizeFramebuffers(Renderer renderer) {
        FBO.destroy();
        FBO = createFrameBuffer(null,depthBufferRequest, renderer, Window.get().width, Window.get().height);
    }

    @Override
    public void render() {
        FBO.bindToWrite();
        shader.use();
        glClear(GL_DEPTH_BUFFER_BIT);
        glClear(GL_COLOR_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        shader.uploadMat4f("projectionMatrix", perspective.getProjectionMatrix());
        shader.uploadMat4f("viewMatrix",perspective.getViewMatrix());

        for(Model model : sceneData.models){
            shader.uploadMat4f("modelMatrix",model.getModelMatrix());
            model.render();
        }


        FBO.detach();
        shader.detach();
        glBindVertexArray(0);
    }
}
