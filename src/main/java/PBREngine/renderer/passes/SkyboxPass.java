package PBREngine.renderer.passes;

import PBREngine.engine.Window;
import PBREngine.engine.scene.SceneData;
import PBREngine.renderer.Renderer;
import PBREngine.renderer.Shader;
import PBREngine.renderer.Shaders;
import PBREngine.renderer.buffers.FrameBuffer;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class SkyboxPass extends RenderPass{
    private static Shader shader = Shaders.skyboxProgram;
    private Renderer renderer;
    private SceneData sceneData;
    private FrameBuffer FBO;
    private int[] colorBufferRequests;
    private int depthBufferRequest;

    public SkyboxPass(Renderer renderer, SceneData sceneData, int[] colorBufferRequests, int depthBufferRequest) {
        if(!shader.isCompiled) shader.compile();
        this.renderer = renderer;
        this.sceneData = sceneData;
        this.colorBufferRequests = colorBufferRequests;
        this.depthBufferRequest = depthBufferRequest;this.colorBufferRequests = colorBufferRequests;
        this.depthBufferRequest = depthBufferRequest;
        FBO = createFrameBuffer(colorBufferRequests,depthBufferRequest, renderer, Window.get().width, Window.get().height);
    }

    @Override
    public void resizeFramebuffers(Renderer renderer) {
        FBO.destroy();
        FBO = createFrameBuffer(colorBufferRequests,depthBufferRequest, renderer, Window.get().width, Window.get().height);
    }

    @Override
    public void render() {
        FBO.bindToWrite();
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);

        shader.use();

        shader.uploadMat4f("cameraViewMatrix",sceneData.camera.getViewMatrixNoTranslation());
        shader.uploadMat4f("cameraProjectionMatrix",sceneData.camera.getProjectionMatrix());
        shader.uploadInt("equirectangularMap",0);

        sceneData.skybox.render();

        FBO.detach();
        shader.detach();
        glBindVertexArray(0);

    }

}
