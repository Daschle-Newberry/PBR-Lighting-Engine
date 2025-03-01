package OpenGL_Basic.renderer.passes;

import OpenGL_Basic.renderer.buffers.Buffer;

public abstract class RenderPass {
    public abstract void render();

    public  abstract Buffer getBuffer();
}
