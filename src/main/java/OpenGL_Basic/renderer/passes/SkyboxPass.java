package OpenGL_Basic.renderer.passes;

import OpenGL_Basic.engine.Camera;
import OpenGL_Basic.engine.CubeMap;
import OpenGL_Basic.engine.Perspective;
import OpenGL_Basic.renderer.Renderer;
import OpenGL_Basic.renderer.Shaders;
import OpenGL_Basic.renderer.buffers.ColorBuffer;
import OpenGL_Basic.renderer.buffers.OutputBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class SkyboxPass implements RenderPass{
    private static String[] dependencies = null;
    private Renderer renderer;
    private Perspective perspective;
    private ColorBuffer outputBuffer;
    public SkyboxPass(Renderer renderer){
        this.renderer = renderer;
        this.perspective = renderer.sceneCamera;
        this.outputBuffer = (ColorBuffer) renderer.outputs.get("RenderOutput");
    }

    @Override
    public void render() {
        outputBuffer.bindToWrite();
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);

        CubeMap skybox = renderer.skybox;
        Shaders.skyboxProgram.use();
        Shaders.skyboxProgram.uploadMat4f("cameraViewMatrix",perspective.getViewMatrixNoTranslation());
        Shaders.skyboxProgram.uploadMat4f("cameraProjectionMatrix",perspective.getProjectionMatrix());
        skybox.render();

        outputBuffer.detach();
        Shaders.skyboxProgram.detach();
        glBindVertexArray(0);

    }
    @Override
    public OutputBuffer getBuffer() {return this.outputBuffer;}

    public String[] getDependencies() {
        return dependencies;
    }

    @Override
    public void sourceDependencies() {

    }
}
