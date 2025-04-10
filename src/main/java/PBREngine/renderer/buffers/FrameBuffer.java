package PBREngine.renderer.buffers;

import PBREngine.engine.Window;


import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {
    private int width,height, fboID;

    public FrameBuffer(Sampler2D[] colorAttachments, Sampler2D depthAttachment,int width, int height){
        this.height = height;
        this.width = width;

        this.fboID = glGenFramebuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        int[] attachmentLocations = new int[colorAttachments.length];
        for(int i = 0; i < colorAttachments.length; i++){
            glFramebufferTexture2D(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0 + i,GL_TEXTURE_2D,colorAttachments[i].getTextureID(),0);
            attachmentLocations[i] = GL_COLOR_ATTACHMENT0 + i;
        }

        glDrawBuffers(attachmentLocations);


        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_DEPTH_ATTACHMENT,GL_TEXTURE_2D, depthAttachment.getTextureID(), 0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            throw new RuntimeException("Unknown Framebuffer error");
        }
    }

    public FrameBuffer(Sampler2D[] colorAttachments,int width, int height){
        this.height = height;
        this.width = width;

        this.fboID = glGenFramebuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        int[] attachmentLocations = new int[colorAttachments.length];
        for(int i = 0; i < colorAttachments.length; i++){
            System.out.println(colorAttachments[i].getTextureID());
            glFramebufferTexture2D(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0 + i,GL_TEXTURE_2D,colorAttachments[i].getTextureID(),0);
            attachmentLocations[i] = GL_COLOR_ATTACHMENT0 + i;

        }

        glDrawBuffers(attachmentLocations);


        int RBO = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER,RBO);
        glRenderbufferStorage(GL_RENDERBUFFER,GL_DEPTH24_STENCIL8, Window.get().width,Window.get().height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER,GL_DEPTH_STENCIL_ATTACHMENT,GL_RENDERBUFFER,RBO);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            throw new RuntimeException("Unknown Framebuffer error");
        }
    }


    public FrameBuffer(Sampler2D depthAttachment,int width, int height){
        this.height = height;
        this.width = width;

        this.fboID = glGenFramebuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_DEPTH_ATTACHMENT,GL_TEXTURE_2D, depthAttachment.getTextureID(), 0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            throw new RuntimeException("Unknown Framebuffer error");
        }
    }
    public void bindToWrite(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER,fboID);
        glViewport(0,0,width,height);
    }
    public void detach(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER,0);
    }

    public void destroy(){
        glDeleteFramebuffers(fboID);
    }



}
