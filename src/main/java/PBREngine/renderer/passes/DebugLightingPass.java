package PBREngine.renderer.passes;

import PBREngine.engine.Window;
import PBREngine.engine.scene.elements.model.Model;
import PBREngine.engine.scene.SceneData;
import PBREngine.renderer.Renderer;
import PBREngine.renderer.Shader;
import PBREngine.renderer.Shaders;
import PBREngine.renderer.buffers.FrameBuffer;

import static PBREngine.renderer.Renderer.T_ALBEDO;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class DebugLightingPass extends RenderPass {
    private static Shader shader = Shaders.debugProgram;
    private Renderer renderer;
    private SceneData sceneData;
    private FrameBuffer FBO;
    private int[] colorBufferRequests;
    private int depthBufferRequest;

    public DebugLightingPass(Renderer renderer, SceneData sceneData, int[] colorBufferRequests, int depthBufferRequest) {
        if(!shader.isCompiled) shader.compile();
        this.renderer = renderer;
        this.sceneData = sceneData;
        this.colorBufferRequests = colorBufferRequests;
        this.depthBufferRequest = depthBufferRequest;
        FBO = createFrameBuffer(colorBufferRequests, depthBufferRequest, renderer, Window.get().width, Window.get().height);
        renderer.createBRDFLUT();
    }

    @Override
    public void resizeFramebuffers(Renderer renderer) {
        FBO.destroy();
        FBO = createFrameBuffer(colorBufferRequests,depthBufferRequest, renderer, Window.get().width, Window.get().height);
    }

    @Override
    public void render() {
        FBO.bindToWrite();
        glClear(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

       shader.use();
       shader.uploadMat4f("cameraProjectionMatrix",sceneData.camera.getProjectionMatrix());
       shader.uploadMat4f("cameraViewMatrix",sceneData.camera.getViewMatrix());
       shader.uploadInt("albedo", T_ALBEDO);

       for(Model model : sceneData.models){
           shader.uploadMat4f("modelMatrix",model.getModelMatrix());
           model.bindMaterial();
           model.render();
       }


        FBO.detach();
        shader.detach();
        glBindVertexArray(0);
    }
}
