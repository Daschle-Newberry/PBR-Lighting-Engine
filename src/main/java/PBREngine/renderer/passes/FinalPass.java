package PBREngine.renderer.passes;

import PBREngine.engine.Window;
import PBREngine.renderer.Renderer;
import PBREngine.renderer.Shader;
import PBREngine.renderer.Shaders;
import PBREngine.renderer.buffers.FrameBuffer;
import PBREngine.util.Quad;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static PBREngine.renderer.Renderer.B_COLORTEX0;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class FinalPass extends RenderPass {
    private static Shader shader = Shaders.screenProgram;
    private Renderer renderer;
    private FrameBuffer FBO;

    private int screenQuad;

    public FinalPass(Renderer renderer,int[] colorBufferRequest,int depthBufferRequest){
        if(!shader.isCompiled) shader.compile();
        this.renderer = renderer;
        FBO = createFrameBuffer(colorBufferRequest,depthBufferRequest,renderer);

        screenQuad  = glGenVertexArrays();
        glBindVertexArray(screenQuad);

        FloatBuffer quadVertexBuffer = BufferUtils.createFloatBuffer(Quad.quadVertices.length);
        quadVertexBuffer.put(Quad.quadVertices).flip();

        //VBO
        int quadVBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER,quadVBO);

        glBufferData(GL_ARRAY_BUFFER,quadVertexBuffer,GL_STATIC_DRAW);

        // XYZ
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0,2,GL_FLOAT,false,4 * 4,0);

        // RGB
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1,2,GL_FLOAT,false,4 * 4,2 * 4);
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

        glBindVertexArray(screenQuad);
        glDrawArrays(GL_TRIANGLES,0,6);

        shader.detach();
        glBindVertexArray(0);

        FBO.bindToWrite();
        glClear(GL_COLOR_BUFFER_BIT);
        FBO.detach();
    }




}
