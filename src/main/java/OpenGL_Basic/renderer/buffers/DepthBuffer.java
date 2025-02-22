package OpenGL_Basic.renderer.buffers;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL30.*;

public class DepthBuffer extends OutputBuffer{
    private int mapWidth,mapHeight,FBO,outputTexture;



    public DepthBuffer(int width, int height){
        FBO = glGenFramebuffers();
        outputTexture = glGenTextures();
        mapWidth = width;
        mapHeight = height;

        glBindTexture(GL_TEXTURE_2D, outputTexture);
        glTexImage2D(GL_TEXTURE_2D,0,GL_DEPTH_COMPONENT,width,height,0,GL_DEPTH_COMPONENT,GL_FLOAT,(FloatBuffer) null);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_BORDER);

        glTexParameterfv(GL_TEXTURE_2D,GL_TEXTURE_BORDER_COLOR,new float[]{1.0f,1.0f,1.0f,1.0f});

        glBindFramebuffer(GL_FRAMEBUFFER,FBO);
        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_DEPTH_ATTACHMENT,GL_TEXTURE_2D,outputTexture,0);

        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            assert false : "Framebuffer Error: " + DepthBuffer.class.getSimpleName();

        }
        glBindFramebuffer(GL_FRAMEBUFFER,0);
    }

    @Override
    public void bindToWrite(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER,FBO);
        glViewport(0,0,mapWidth,mapHeight);
    }

    @Override
    public void bindToRead(){
        glActiveTexture(GL_TEXTURE6);
        glBindTexture(GL_TEXTURE_2D,outputTexture);
    }

    @Override
    public void detach(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER,0);
    }

    public Vector2f getMapDimensions(){
        return new Vector2f(this.mapWidth,this.mapHeight);
    }

}
