package OpenGL_Basic.engine;

import OpenGL_Basic.renderer.Shaders;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

public class ReflectionProbe {
    private int diffuseID,specularID;
    private Vector3f position;

    public ReflectionProbe(Vector3f position){
        this.position = position;
    }

    public void update(){
//        int FBO = glGenFramebuffers();
//        int RBO = glGenRenderbuffers();
//        diffuseID = glGenTextures();
//        glBindTexture(GL_TEXTURE_CUBE_MAP, diffuseID);
//
//
//        for (int i = 0; i < 6; i++) {
//            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, 32, 32, 0, GL_RGB, GL_FLOAT, 0);
//        }
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//
//
//        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
//        glBindRenderbuffer(GL_RENDERBUFFER, RBO);
//        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, 32, 32);
//
//
//        if (!Shaders.irradianceConvolutionProgram.isCompiled) Shaders.irradianceConvolutionProgram.compile();
//        Shaders.irradianceConvolutionProgram.use();
//
//        Shaders.irradianceConvolutionProgram.uploadInt("environmentMap", 0);
//        Shaders.irradianceConvolutionProgram.uploadMat4f("projectionMatrix", projection);
//
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_CUBE_MAP, skyboxID);
//        glViewport(0, 0, 32, 32);
//        for (int i = 0; i < 6; i++) {
//            Shaders.irradianceConvolutionProgram.uploadMat4f("viewMatrix", views[i]);
//            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, irradianceID, 0);
//            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//            glDrawArrays(GL_TRIANGLES, 0, 36);
//        }
//
//        Shaders.irradianceConvolutionProgram.detach();
//        glBindFramebuffer(GL_FRAMEBUFFER, 0);
//
//        /* SPECULAR */
//
//        specularMap = glGenTextures();
//        glBindTexture(GL_TEXTURE_CUBE_MAP, specularMap);
//        FBO = glGenFramebuffers();
//        RBO = glGenRenderbuffers();
//
//        for (int i = 0; i < 6; i++) {
//            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, 128, 128, 0, GL_RGB, GL_FLOAT, 0);
//        }
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//
//        glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
//
//
//        if (!Shaders.specularConvolutionProgram.isCompiled) Shaders.specularConvolutionProgram.compile();
//        Shaders.specularConvolutionProgram.use();
//
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_CUBE_MAP, skyboxID);
//
//        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
//        glBindRenderbuffer(GL_RENDERBUFFER,RBO);
//
//        glRenderbufferStorage(GL_RENDERBUFFER,GL_DEPTH_COMPONENT24,128,128);
//        glViewport(0,0,128,128);
//
//        Shaders.specularConvolutionProgram.uploadMat4f("projectionMatrix", projection);
//
//        for (int i = 0; i < 6; i++) {
//            Shaders.specularConvolutionProgram.uploadMat4f("viewMatrix", views[i]);
//            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, specularMap, 0);
//            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//            glDrawArrays(GL_TRIANGLES, 0, 36);
//        }
//
//        int maxMipLevels = 5;
//
//        for (int mip = 0; mip < maxMipLevels; mip++) {
//            int mipWidth = (int) (128 * Math.pow(0.5f,mip));
//            int mipHeight = (int) (128 * Math.pow(0.5f,mip));
//
//            glBindRenderbuffer(GL_RENDERBUFFER,RBO);
//            glRenderbufferStorage(GL_RENDERBUFFER,GL_DEPTH_COMPONENT24,mipWidth,mipHeight);
//            glViewport(0,0,mipWidth,mipHeight);
//
//            float roughness = mip / (float)(maxMipLevels - 1);
//            Shaders.specularConvolutionProgram.uploadFloat("roughness",roughness);
//            for (int i = 0; i < 6; i++) {
//                Shaders.specularConvolutionProgram.uploadMat4f("viewMatrix", views[i]);
//                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, specularMap, mip);
//                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//                glDrawArrays(GL_TRIANGLES, 0, 36);
//            }
//        }
//
//        Shaders.specularConvolutionProgram.detach();
//        glBindFramebuffer(GL_FRAMEBUFFER,0);
    }

    public void render(){
        glDrawArrays(GL_TRIANGLES,0,8);
    }

    public void bindDiffuseMap(){
        glActiveTexture(GL_TEXTURE5);
        glBindTexture(GL_TEXTURE_CUBE_MAP,diffuseID);
    }
    public void bindSpecularMap(){
        glActiveTexture(GL_TEXTURE6);
        glBindTexture(GL_TEXTURE_CUBE_MAP,specularID);
    }

    public Vector3f getPosition(){return this.position;}
}
