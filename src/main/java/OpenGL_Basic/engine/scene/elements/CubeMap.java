package OpenGL_Basic.engine.scene.elements;

import OpenGL_Basic.renderer.Shaders;
import OpenGL_Basic.util.ImageLoader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class CubeMap {
    public static Matrix4f projection = new Matrix4f().perspective((float) Math.toRadians(90), 1, .1f, 100.0f);
    public static Matrix4f[] views = new Matrix4f[]{
            new Matrix4f().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f)),
            new Matrix4f().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(-1.0f, 0.0f, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f)),
            new Matrix4f().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f)),
            new Matrix4f().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f)),
            new Matrix4f().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(0.0f, -1.0f, 0.0f)),
            new Matrix4f().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f), new Vector3f(0.0f, -1.0f, 0.0f))
    };

    private static boolean brdfIsComputed;
    private static int brdfLUT;
    private int environmentID,diffuseID,specularID;


    public CubeMap(String filePath) {
        int equirectangularImage = glGenTextures();

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        FloatBuffer textureIMG = ImageLoader.loadHDRImage(filePath,width,height,channels);

        glBindTexture(GL_TEXTURE_2D,equirectangularImage);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB16F,width.get(0),height.get(0),0,GL_RGB,GL_FLOAT,textureIMG);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);


        convertToCubeMap(equirectangularImage, width.get(0),height.get(0));
        generateMaps();

        width.clear();
        height.clear();
        channels.clear();
        textureIMG.clear();
    }

    public CubeMap(){};

    public void setEnvironmentID(int environmentID){
        this.environmentID = environmentID;
    }

    public void convertToCubeMap(int equirectangularImage, int width, int height) {
        int FBO = glGenFramebuffers();
        int RBO = glGenRenderbuffers();

        int cubeWidth = width / 4;
        int cubeHeight = height / 2;

        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
        glBindRenderbuffer(GL_RENDERBUFFER, RBO);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, cubeWidth, cubeHeight);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, RBO);

        environmentID = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, environmentID);

        for (int i = 0; i < 6; i++) {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, cubeWidth, cubeHeight, 0, GL_RGB, GL_FLOAT, 0);
        }
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        if (!Shaders.cubeMappingProgram.isCompiled) Shaders.cubeMappingProgram.compile();

        Shaders.cubeMappingProgram.use();
        Shaders.cubeMappingProgram.uploadInt("equirectangularMap", 0);
        Shaders.cubeMappingProgram.uploadMat4f("projectionMatrix", projection);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, equirectangularImage);

        glViewport(0, 0, cubeWidth, cubeHeight);
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
        for (int i = 0; i < 6; i++) {
            Shaders.cubeMappingProgram.uploadMat4f("viewMatrix", views[i]);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, environmentID, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glDrawArrays(GL_TRIANGLES, 0, 36);

        }

        Shaders.cubeMappingProgram.detach();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    public void generateMaps(){
        int FBO = glGenFramebuffers();
        int RBO = glGenRenderbuffers();

        diffuseID = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, diffuseID);

        for (int i = 0; i < 6; i++) {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, 32, 32, 0, GL_RGB, GL_FLOAT, 0);
        }
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);


        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
        glBindRenderbuffer(GL_RENDERBUFFER, RBO);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, 32, 32);


        if (!Shaders.irradianceConvolutionProgram.isCompiled) Shaders.irradianceConvolutionProgram.compile();
        Shaders.irradianceConvolutionProgram.use();

        Shaders.irradianceConvolutionProgram.uploadInt("environmentMap", 0);
        Shaders.irradianceConvolutionProgram.uploadMat4f("projectionMatrix", projection);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, environmentID);
        glViewport(0, 0, 32, 32);
        for (int i = 0; i < 6; i++) {
            Shaders.irradianceConvolutionProgram.uploadMat4f("viewMatrix", views[i]);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, diffuseID, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glDrawArrays(GL_TRIANGLES, 0, 36);
        }

        Shaders.irradianceConvolutionProgram.detach();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        /* SPECULAR */

        specularID = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, specularID);
        FBO = glGenFramebuffers();
        RBO = glGenRenderbuffers();

        for (int i = 0; i < 6; i++) {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, 128, 128, 0, GL_RGB, GL_FLOAT, 0);
        }
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glGenerateMipmap(GL_TEXTURE_CUBE_MAP);


        if (!Shaders.specularConvolutionProgram.isCompiled) Shaders.specularConvolutionProgram.compile();
        Shaders.specularConvolutionProgram.use();

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, environmentID);

        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
        glBindRenderbuffer(GL_RENDERBUFFER,RBO);

        glRenderbufferStorage(GL_RENDERBUFFER,GL_DEPTH_COMPONENT24,128,128);
        glViewport(0,0,128,128);

        Shaders.specularConvolutionProgram.uploadMat4f("projectionMatrix", projection);

        for (int i = 0; i < 6; i++) {
            Shaders.specularConvolutionProgram.uploadMat4f("viewMatrix", views[i]);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, specularID, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glDrawArrays(GL_TRIANGLES, 0, 36);
        }

        int maxMipLevels = 5;

        for (int mip = 0; mip < maxMipLevels; mip++) {
            int mipWidth = (int) (128 * Math.pow(0.5f,mip));
            int mipHeight = (int) (128 * Math.pow(0.5f,mip));

            glBindRenderbuffer(GL_RENDERBUFFER,RBO);
            glRenderbufferStorage(GL_RENDERBUFFER,GL_DEPTH_COMPONENT24,mipWidth,mipHeight);
            glViewport(0,0,mipWidth,mipHeight);

            float roughness = mip / (float)(maxMipLevels - 1);
            Shaders.specularConvolutionProgram.uploadFloat("roughness",roughness);
            for (int i = 0; i < 6; i++) {
                Shaders.specularConvolutionProgram.uploadMat4f("viewMatrix", views[i]);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, specularID, mip);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glDrawArrays(GL_TRIANGLES, 0, 36);
            }
        }

        Shaders.specularConvolutionProgram.detach();
        glBindFramebuffer(GL_FRAMEBUFFER,0);
    }

    public void render(){
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP,environmentID);
        glDrawArrays(GL_TRIANGLES,0,36);
    }

    public void bindDiffuseMap(){
        glActiveTexture(GL_TEXTURE5);
        glBindTexture(GL_TEXTURE_CUBE_MAP,diffuseID);
    }
    public void bindSpecularMap(){
        glActiveTexture(GL_TEXTURE6);
        glBindTexture(GL_TEXTURE_CUBE_MAP,specularID);
    }
}
