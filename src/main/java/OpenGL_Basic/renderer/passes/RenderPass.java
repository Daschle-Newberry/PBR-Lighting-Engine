package OpenGL_Basic.renderer.passes;

import OpenGL_Basic.renderer.buffers.OutputBuffer;

public interface RenderPass {
    void render();
    void sourceDependencies();

    String[] getDependencies();
    OutputBuffer getBuffer();
}
