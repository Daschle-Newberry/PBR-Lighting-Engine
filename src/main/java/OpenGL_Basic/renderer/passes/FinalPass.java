package OpenGL_Basic.renderer.passes;

import OpenGL_Basic.engine.Window;
import OpenGL_Basic.renderer.RenderPass;
import OpenGL_Basic.renderer.Shaders;
import OpenGL_Basic.renderer.buffers.ScreenBuffer;
import OpenGL_Basic.util.Quad;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class FinalPass extends RenderPass {
    private ScreenBuffer screenBuffer;
    private int screenQuad;

    public FinalPass(ScreenBuffer screenBuffer){
        this.screenBuffer = screenBuffer;
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
        screenBuffer.bindToRead();
        glBindFramebuffer(GL_FRAMEBUFFER,0);
        glViewport(0,0, Window.get().width,Window.get().height);
        glPolygonMode(GL_FRONT, GL_FILL);
        glDisable(GL_DEPTH_TEST);

        Shaders.screenProgram.use();

        Shaders.screenProgram.uploadInt("screenTexture",0);

        glBindVertexArray(screenQuad);
        glDrawArrays(GL_TRIANGLES,0,6);


        Shaders.screenProgram.detach();
        glBindVertexArray(0);
    }
}
