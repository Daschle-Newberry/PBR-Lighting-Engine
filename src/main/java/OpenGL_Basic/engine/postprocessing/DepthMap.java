package OpenGL_Basic.engine.postprocessing;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL30.*;

public class DepthMap {
    private int mapWidth,mapHeight,FBO,shadowMap;
    private Matrix4f depthProjection;



    public DepthMap(int width, int height){
        FBO = glGenFramebuffers();
        shadowMap = glGenTextures();
        mapWidth = width;
        mapHeight = height;

        glBindTexture(GL_TEXTURE_2D, shadowMap);
        glTexImage2D(GL_TEXTURE_2D,0,GL_DEPTH_COMPONENT,width,height,0,GL_DEPTH_COMPONENT,GL_FLOAT,(FloatBuffer) null);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_BORDER);

        glTexParameterfv(GL_TEXTURE_2D,GL_TEXTURE_BORDER_COLOR,new float[]{1.0f,1.0f,1.0f,1.0f});

        glBindFramebuffer(GL_FRAMEBUFFER,FBO);
        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_DEPTH_ATTACHMENT,GL_TEXTURE_2D,shadowMap,0);

        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            assert false : "Framebuffer Error: " + DepthMap.class.getSimpleName();

        }
        glBindFramebuffer(GL_FRAMEBUFFER,0);
    }

    public void setProjectionOrtho(){
        depthProjection = new Matrix4f().identity();
        depthProjection.ortho(-5,5,-5,5,0.1f,20f);
    }



    public Matrix4f getProjectionMatrix(){return this.depthProjection;}

    public void bindToWrite(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER,FBO);
        glViewport(0,0,mapWidth,mapHeight);
    }
    public void bindToRead(){
        glActiveTexture(GL_TEXTURE6);
        glBindTexture(GL_TEXTURE_2D,shadowMap);

    }

    public Vector2f getMapDimensions(){
        return new Vector2f(this.mapWidth,this.mapHeight);
    }

}
