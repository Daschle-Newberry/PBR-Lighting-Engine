package OpenGL_Basic.engine.postprocessing;

import org.joml.Matrix4f;
import org.joml.Vector2i;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL30.*;

public class ShadowMap {
    public int FBO;
    private int mapWidth,mapHeight;
    private int shadowMap;
    private Matrix4f shadowProjection;

    public ShadowMap(int width, int height){
        FBO = glGenFramebuffers();
        shadowMap = glGenTextures();
        mapWidth = width;
        mapHeight = height;

        glBindTexture(GL_TEXTURE_2D, shadowMap);
        glTexImage2D(GL_TEXTURE_2D,0,GL_DEPTH_COMPONENT,width,height,0,GL_DEPTH_COMPONENT,GL_FLOAT,(FloatBuffer) null);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);

        glBindFramebuffer(GL_FRAMEBUFFER,FBO);
        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_DEPTH_ATTACHMENT,GL_TEXTURE_2D,shadowMap,0);

        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            assert false : "Framebuffer Error: " + ShadowMap.class.getSimpleName();

        }

        glBindFramebuffer(GL_FRAMEBUFFER,0);


        setShadowMapProjectionOrtho();
    }
    public void setShadowMapProjectionPerspective(){
        shadowProjection = new Matrix4f().identity();
        shadowProjection.perspective(45,(float) mapWidth/mapHeight,0.1f,100.0f);
    }

    public void setShadowMapProjectionOrtho(){
        shadowProjection = new Matrix4f().identity();
        shadowProjection.ortho(-5,5,-5,5,0.1f,10f);
    }

    public Matrix4f getProjectionMatrix(){
        return this.shadowProjection;
    }

    public void bindToWrite(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER,FBO);
        glViewport(0,0,mapWidth,mapHeight);
    }
    public void bindToRead(){
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D,shadowMap);
    }

    public Vector2i getMapDimensions(){
        return new Vector2i(this.mapWidth,this.mapHeight);
    }

}
