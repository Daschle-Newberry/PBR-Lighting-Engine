package OpenGL_Basic.renderer.passes;

import OpenGL_Basic.engine.SceneData;
import OpenGL_Basic.engine.gameobjects.Camera;
import OpenGL_Basic.engine.CubeMap;
import OpenGL_Basic.engine.gameobjects.Perspective;
import OpenGL_Basic.renderer.Renderer;
import OpenGL_Basic.renderer.Shader;
import OpenGL_Basic.renderer.Shaders;
import OpenGL_Basic.renderer.buffers.FrameBuffer;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class SkyboxPass extends RenderPass{
    private static Shader shader = Shaders.skyboxProgram;
    private Renderer renderer;
    private SceneData sceneData;
    private FrameBuffer FBO;
    public SkyboxPass(Renderer renderer, SceneData sceneData, int[] colorBufferRequest, int depthBufferRequest) {
        if(!shader.isCompiled) shader.compile();
        this.renderer = renderer;
        this.sceneData = sceneData;
        FBO = createFrameBuffer(colorBufferRequest,depthBufferRequest,renderer);
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
