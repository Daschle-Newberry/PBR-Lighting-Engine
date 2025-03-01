package OpenGL_Basic.renderer.passes;

import OpenGL_Basic.engine.Window;
import OpenGL_Basic.renderer.Renderer;
import OpenGL_Basic.renderer.Shaders;
import OpenGL_Basic.renderer.buffers.RenderTarget;
import OpenGL_Basic.renderer.buffers.Buffer;
import OpenGL_Basic.util.Quad;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static OpenGL_Basic.renderer.Renderer.R_OUTPUT_BUFFER;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class FinalPass extends RenderPass {
    private static int[] dependencies = null;
    private Renderer renderer;
    private RenderTarget screenBuffer;
    private int screenQuad;

    public FinalPass(Renderer renderer){
        this.renderer = renderer;

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
        glClear(GL_COLOR_BUFFER_BIT);
        glClear(GL_DEPTH_BUFFER_BIT);

        glViewport(0,0, Window.get().width,Window.get().height);
        glPolygonMode(GL_FRONT, GL_FILL);
        glDisable(GL_DEPTH_TEST);

        Shaders.screenProgram.use();

        Shaders.screenProgram.uploadInt("screenTexture",0);

        glBindVertexArray(screenQuad);
        glDrawArrays(GL_TRIANGLES,0,6);


        screenBuffer.bindToWrite();
        glClear(GL_COLOR_BUFFER_BIT);
        glClear(GL_DEPTH_BUFFER_BIT);

        Shaders.screenProgram.detach();
        glBindVertexArray(0);
    }
    @Override
    public Buffer getBuffer() {return null;}

    public int[] getDependencies() {
        return dependencies;
    }


}
