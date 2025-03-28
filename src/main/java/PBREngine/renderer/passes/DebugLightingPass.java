package PBREngine.renderer.passes;

import PBREngine.engine.scene.elements.model.Model;
import PBREngine.engine.scene.SceneData;
import PBREngine.renderer.Renderer;
import PBREngine.renderer.Shader;
import PBREngine.renderer.Shaders;
import PBREngine.renderer.buffers.FrameBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class DebugLightingPass extends RenderPass {
    private static Shader shader = Shaders.debugProgram;
    private Renderer renderer;
    private SceneData sceneData;
    private FrameBuffer FBO;

    public DebugLightingPass(Renderer renderer, SceneData sceneData, int[] colorBufferRequest, int depthBufferRequest) {
        if(!shader.isCompiled) shader.compile();
        this.renderer = renderer;
        this.sceneData = sceneData;
        FBO = createFrameBuffer(colorBufferRequest,depthBufferRequest,renderer);
        renderer.createBRDFLUT();
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
       shader.uploadVec3f("lightDirection",sceneData.sceneLight.getLightFront());

       for(Model model : sceneData.models){
           shader.uploadMat4f("modelMatrix",model.getModelMatrix());
           model.render();
       }


        FBO.detach();
        shader.detach();
        glBindVertexArray(0);
    }
}
