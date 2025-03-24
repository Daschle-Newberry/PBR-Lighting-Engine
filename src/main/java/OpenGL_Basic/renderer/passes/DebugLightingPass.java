package OpenGL_Basic.renderer.passes;

import OpenGL_Basic.engine.Model;
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
    private Renderer renderer;
    private FrameBuffer FBO;

    public DebugLightingPass(Renderer renderer, int[] colorBufferRequest, int depthBufferRequest) {
        if(!shader.isCompiled) shader.compile();
        this.renderer = renderer;
        FBO = createFrameBuffer(colorBufferRequest,depthBufferRequest,renderer);
    }

    @Override
    public void render() {
        FBO.bindToWrite();
        shader.use();
        glClear(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        shader.uploadMat4f("cameraProjectionMatrix", renderer.sceneCamera.getProjectionMatrix());
        shader.uploadMat4f("cameraViewMatrix", renderer.sceneCamera.getViewMatrix());

        shader.uploadInt("albedo",T_ALBEDO);


        for(Model model : renderer.models){
            shader.uploadMat4f("modelMatrix",model.getModelMatrix());
            model.bindMaterial();
            model.render();
        }

        FBO.detach();
        shader.detach();
        glBindVertexArray(0);
    }
}
