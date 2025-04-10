package PBREngine.renderer.passes;

import PBREngine.engine.Window;
import PBREngine.renderer.Renderer;
import PBREngine.renderer.Shader;
import PBREngine.renderer.Shaders;
import PBREngine.renderer.buffers.FrameBuffer;

import static PBREngine.renderer.Renderer.B_COLORTEX0;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class FinalPass extends RenderPass {
    private static Shader shader = Shaders.screenProgram;
    private Renderer renderer;
    private FrameBuffer FBO;
    private int[] colorBufferRequests;
    private int depthBufferRequest;

    public FinalPass(Renderer renderer,int[] colorBufferRequests,int depthBufferRequest){
        if(!shader.isCompiled) shader.compile();
        this.renderer = renderer;
        this.colorBufferRequests = colorBufferRequests;
        this.depthBufferRequest = depthBufferRequest;
        FBO = createFrameBuffer(colorBufferRequests,depthBufferRequest,renderer, Window.get().width, Window.get().height);
    }

    @Override
    public void resizeFramebuffers(Renderer renderer) {
        FBO.destroy();
        FBO = createFrameBuffer(colorBufferRequests,depthBufferRequest, renderer, Window.get().width, Window.get().height);
    }

    @Override
    public void render() {
        glBindFramebuffer(GL_FRAMEBUFFER,0);
        glClear(GL_COLOR_BUFFER_BIT);
        glClear(GL_DEPTH_BUFFER_BIT);

        glViewport(0,0, Window.get().width,Window.get().height);
        glPolygonMode(GL_FRONT, GL_FILL);
        glDisable(GL_DEPTH_TEST);

        shader.use();

        shader.uploadInt("colortex0",0);
        renderer.getBuffer(B_COLORTEX0).bindTo(0);

        glDrawArrays(GL_TRIANGLES,0,6);

        shader.detach();
        glBindVertexArray(0);

        FBO.bindToWrite();
        glClear(GL_COLOR_BUFFER_BIT);
        FBO.detach();
    }




}
