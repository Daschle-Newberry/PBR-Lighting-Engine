package OpenGL_Basic.renderer.buffers;

import OpenGL_Basic.engine.Window;


import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {
    private int mapWidth,mapHeight, fboID;

    public FrameBuffer(Texture[] colorAttachments, Texture depthAttachment){
        this.fboID = glGenFramebuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
        int count = 0;
        for(Texture attachment : colorAttachments){
            glFramebufferTexture2D(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0 + count,GL_TEXTURE_2D,attachment.getTextureID(),0);
            count ++;
        }

        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_DEPTH_ATTACHMENT + count,GL_TEXTURE_2D, depthAttachment.getTextureID(), 0);
    }

    public FrameBuffer(Texture[] colorAttachments){
        this.fboID = glGenFramebuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
        int count = 0;
        for(Texture attachment : colorAttachments){
            glFramebufferTexture2D(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0 + count,GL_TEXTURE_2D,attachment.getTextureID(),0);
            count ++;
        }

        int RBO = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER,RBO);
        glRenderbufferStorage(GL_RENDERBUFFER,GL_DEPTH24_STENCIL8, Window.get().width,Window.get().height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER,GL_DEPTH_STENCIL_ATTACHMENT,GL_RENDERBUFFER,RBO);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            throw new RuntimeException("Unknown Framebuffer error");
        }
    }


}
