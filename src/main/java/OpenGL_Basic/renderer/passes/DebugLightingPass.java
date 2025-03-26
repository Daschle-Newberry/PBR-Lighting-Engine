package OpenGL_Basic.renderer.passes;

import OpenGL_Basic.engine.Model;
import OpenGL_Basic.engine.SceneData;
import OpenGL_Basic.engine.Window;
import OpenGL_Basic.renderer.Renderer;
import OpenGL_Basic.renderer.Shader;
import OpenGL_Basic.renderer.Shaders;
import OpenGL_Basic.renderer.buffers.Buffer;
import OpenGL_Basic.renderer.buffers.FrameBuffer;
import OpenGL_Basic.renderer.buffers.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static OpenGL_Basic.renderer.Renderer.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class DebugLightingPass extends RenderPass {
    private static Shader shader = Shaders.debugProgram;
    private SceneData sceneData;
    private FrameBuffer FBO;

    public DebugLightingPass(Renderer renderer, SceneData sceneData, int[] colorBufferRequest, int depthBufferRequest) {
        if(!shader.isCompiled) shader.compile();
        this.sceneData = sceneData;
        FBO = createFrameBuffer(colorBufferRequest,depthBufferRequest,renderer);
    }

    @Override
    public void render() {
        FBO.bindToWrite();
        shader.use();
        glClear(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        shader.uploadMat4f("cameraProjectionMatrix", sceneData.camera.getProjectionMatrix());
        shader.uploadMat4f("cameraViewMatrix", sceneData.camera.getViewMatrix());

        shader.uploadInt("albedo",T_ALBEDO);


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
