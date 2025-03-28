package OpenGL_Basic.renderer.buffers;

import OpenGL_Basic.renderer.Shaders;
import org.joml.Vector2f;

import static OpenGL_Basic.renderer.Renderer.B_BRDFLUT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.*;

public class BRDFLUT implements Sampler2D {
    private int brdfLUTID;

    public BRDFLUT(){
        int FBO = glGenFramebuffers();
        int RBO = glGenRenderbuffers();

        brdfLUTID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,brdfLUTID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RG16F, 512, 512, 0, GL_RG, GL_FLOAT, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glBindFramebuffer(GL_FRAMEBUFFER,FBO);
        glBindRenderbuffer(GL_RENDERBUFFER,RBO);
        glRenderbufferStorage(GL_RENDERBUFFER,GL_DEPTH_COMPONENT24,512,512);
        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0,GL_TEXTURE_2D,brdfLUTID,0);

        if(!Shaders.precomputeBRDFProgram.isCompiled) Shaders.precomputeBRDFProgram.compile();

        glViewport(0,0,512,512);
        Shaders.precomputeBRDFProgram.use();
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
        glDrawArrays(GL_TRIANGLES,0,6);

        Shaders.precomputeBRDFProgram.detach();
        glBindFramebuffer(GL_FRAMEBUFFER,0);
    }

    @Override
    public void bindTo(int binding) {

    }

    @Override
    public void bind() {
        glActiveTexture(GL_TEXTURE0 + B_BRDFLUT);
        glBindTexture(GL_TEXTURE_2D,brdfLUTID);
    }

    @Override
    public int getTextureID() {
        return 0;
    }

    @Override
    public Vector2f getDimensions() {
        return null;
    }
}
