package PBREngine.renderer.passes;

import PBREngine.engine.Window;
import PBREngine.renderer.Renderer;
import PBREngine.renderer.buffers.FrameBuffer;
import PBREngine.renderer.buffers.Sampler2D;
import PBREngine.renderer.buffers.Texture;

import static PBREngine.renderer.Renderer.B_NONE;

public abstract class RenderPass {
    public abstract void render();
    public abstract void resizeFramebuffers(Renderer renderer);
    protected FrameBuffer createFrameBuffer(int[] colorBufferRequest, int depthBufferRequest,Renderer renderer, int width, int height){

        System.out.println("Framebuffer Created");
        FrameBuffer FBO;
        if(depthBufferRequest == B_NONE && colorBufferRequest != null){
            Sampler2D[] colorBuffers = new Texture[colorBufferRequest.length];
            for (int i = 0; i < colorBufferRequest.length; i++) {
                colorBuffers[i] = renderer.ensureColorBuffer(colorBufferRequest[i]);
            }
            FBO = new FrameBuffer(colorBuffers,width,height);

        }else if(depthBufferRequest != B_NONE && colorBufferRequest != null){
            Sampler2D[] colorBuffers = new Texture[colorBufferRequest.length];
            for (int i = 0; i < colorBufferRequest.length; i++) {
                colorBuffers[i] = renderer.ensureColorBuffer(colorBufferRequest[i]);
            }
            Sampler2D depthBuffer = renderer.ensureDepthBuffer(depthBufferRequest);
            FBO = new FrameBuffer(colorBuffers,depthBuffer, PBREngine.engine.Window.get().width, Window.get().height);

        }else if(depthBufferRequest != B_NONE){
            Sampler2D depthBuffer = renderer.ensureDepthBuffer(depthBufferRequest);
            FBO = new FrameBuffer(depthBuffer, PBREngine.engine.Window.get().width, Window.get().height);

        }else{
            throw new RuntimeException("Cannot Create Empty FBO!");
        }

        return FBO;
    }
}
