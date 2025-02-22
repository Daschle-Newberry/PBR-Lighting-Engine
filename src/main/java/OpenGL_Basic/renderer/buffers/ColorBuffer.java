package OpenGL_Basic.renderer.buffers;


import OpenGL_Basic.engine.Window;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class ColorBuffer extends OutputBuffer{
    public int FBO;
    private int RBO;
    private int outputTexture;

    public ColorBuffer(){
        FBO = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER,FBO);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE){
            System.out.println("Framebuffer Initalized");
        }

        outputTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,outputTexture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, Window.get().width,Window.get().height, 0, GL_RGB, GL_UNSIGNED_BYTE, (FloatBuffer) null);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, outputTexture, 0);

        RBO = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER,RBO);
        glRenderbufferStorage(GL_RENDERBUFFER,GL_DEPTH24_STENCIL8,Window.get().width,Window.get().height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER,GL_DEPTH_STENCIL_ATTACHMENT,GL_RENDERBUFFER,RBO);


        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            System.out.println("Framebuffer Failure");

        }

        glBindFramebuffer(GL_FRAMEBUFFER,0);
        glViewport(0,0,Window.get().width,Window.get().height);
    }

    @Override
    public void bindToWrite(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER,FBO);
        glViewport(0,0,Window.get().width, Window.get().height);
    }
    @Override
    public void bindToRead(){
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,outputTexture);
    }

    @Override
    public void detach(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER,0);
    }

}
