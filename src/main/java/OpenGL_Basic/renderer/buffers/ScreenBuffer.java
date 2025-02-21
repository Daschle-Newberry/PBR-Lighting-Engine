package OpenGL_Basic.renderer.buffers;


import OpenGL_Basic.engine.Window;
import OpenGL_Basic.renderer.Shaders;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ScreenBuffer extends OutputBuffer{
    public int FBO;
    private int RBO;
    private int textureColorBuffer;

    public ScreenBuffer(){
        FBO = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER,FBO);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE){
            System.out.println("Framebuffer Initalized");
        }

        textureColorBuffer = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,textureColorBuffer);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, Window.get().width,Window.get().height, 0, GL_RGB, GL_UNSIGNED_BYTE, (FloatBuffer) null);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureColorBuffer, 0);

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
    public void framebufferSizeCallback(long window,int width, int height){
        glBindTexture(GL_TEXTURE_2D,textureColorBuffer);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, (FloatBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureColorBuffer, 0);

        glBindRenderbuffer(GL_RENDERBUFFER,RBO);
        glRenderbufferStorage(GL_RENDERBUFFER,GL_DEPTH24_STENCIL8,width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER,GL_DEPTH_STENCIL_ATTACHMENT,GL_RENDERBUFFER,RBO);

        glViewport(0,0,width,height);

    }
//    public void render(){
//        glBindFramebuffer(GL_FRAMEBUFFER,0);
//        glViewport(0,0,Window.get().width,Window.get().height);
//        glPolygonMode(GL_FRONT, GL_FILL);
//
//        glDisable(GL_DEPTH_TEST);
//
//        glClear(GL_COLOR_BUFFER_BIT);
//
//        glBindVertexArray(sc);
//
//        Shaders.screenProgram.use();
//
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D,textureColorBuffer);
//
//        Shaders.screenProgram.uploadInt("screenTexture",0);
//
//        glDrawArrays(GL_TRIANGLES,0,6);
//
//
//        Shaders.screenProgram.detach();
//        glBindVertexArray(0);
//    }
    @Override
    public void bindToWrite(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER,FBO);
        glViewport(0,0,Window.get().width, Window.get().height);
    }
    @Override
    public void bindToRead(){
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,textureColorBuffer);
    }

    @Override
    public void detach(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER,0);
    }

}
