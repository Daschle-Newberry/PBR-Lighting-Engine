package OpenGL_Basic.renderer.passes;

import OpenGL_Basic.engine.Window;
import OpenGL_Basic.renderer.Renderer;
import OpenGL_Basic.renderer.buffers.FrameBuffer;
import OpenGL_Basic.renderer.buffers.Sampler2D;
import OpenGL_Basic.renderer.buffers.Texture;

import java.awt.*;

import static OpenGL_Basic.renderer.Renderer.B_NONE;

public abstract class RenderPass {
    public abstract void render();

    protected FrameBuffer createFrameBuffer(int[] colorBufferRequest, int depthBufferRequest,Renderer renderer){

        FrameBuffer FBO;
        if(depthBufferRequest == B_NONE && colorBufferRequest != null){
            Sampler2D[] colorBuffers = new Texture[colorBufferRequest.length];
            for (int i = 0; i < colorBufferRequest.length; i++) {
                colorBuffers[i] = renderer.ensureColorBuffer(colorBufferRequest[i]);
            }
            FBO = new FrameBuffer(colorBuffers,2560,1440);

        }else if(depthBufferRequest != B_NONE && colorBufferRequest != null){
            Sampler2D[] colorBuffers = new Texture[colorBufferRequest.length];
            for (int i = 0; i < colorBufferRequest.length; i++) {
                colorBuffers[i] = renderer.ensureColorBuffer(colorBufferRequest[i]);
            }
            Sampler2D depthBuffer = renderer.ensureDepthBuffer(depthBufferRequest);
            FBO = new FrameBuffer(colorBuffers,depthBuffer, OpenGL_Basic.engine.Window.get().width, Window.get().height);

        }else if(depthBufferRequest != B_NONE){
            Sampler2D depthBuffer = renderer.ensureDepthBuffer(depthBufferRequest);
            FBO = new FrameBuffer(depthBuffer, OpenGL_Basic.engine.Window.get().width, Window.get().height);

        }else{
            throw new RuntimeException("Cannot Create Empty FBO!");
        }

        return FBO;
    }
}
