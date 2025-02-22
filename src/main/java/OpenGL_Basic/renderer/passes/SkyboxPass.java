package OpenGL_Basic.renderer.passes;

import OpenGL_Basic.engine.Camera;
import OpenGL_Basic.renderer.RenderPass;
import OpenGL_Basic.renderer.Shaders;
import OpenGL_Basic.renderer.buffers.ScreenBuffer;

import javax.swing.*;

import static org.lwjgl.opengl.GL11.*;

public class SkyboxPass extends RenderPass {
    private ScreenBuffer outputBuffer;
    private Renderer renderer;

    public SkyboxPass(Renderer renderer, ScreenBuffer outputBuffer){
        this.outputBuffer = outputBuffer;
        this.renderer = renderer;
    }
    @Override
    public void render() {
        outputBuffer.bindToWrite();
        glClear(GL_DEPTH_BUFFER_BIT);
        glDisable(GL_CULL_FACE);

        Camera sceneCamera = renderer.sceneCamera;

        Shaders.skyboxProgram.use();
        Shaders.skyboxProgram.uploadMat4f("cameraViewMatrix",camera.getViewMatrixNoTranslation());
        Shaders.skyboxProgram.uploadMat4f("cameraProjectionMatrix",camera.getProjectionMatrix());
        skybox.render();
    }
}
