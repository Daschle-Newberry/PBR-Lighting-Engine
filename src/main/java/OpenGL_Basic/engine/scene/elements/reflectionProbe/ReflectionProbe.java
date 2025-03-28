package OpenGL_Basic.engine.scene.elements.reflectionProbe;

import OpenGL_Basic.engine.scene.SceneData;
import OpenGL_Basic.engine.scene.elements.CubeMap;
import OpenGL_Basic.engine.scene.elements.model.Model;
import OpenGL_Basic.renderer.Shaders;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static OpenGL_Basic.renderer.Renderer.T_ALBEDO;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

public class ReflectionProbe {


    private int environmentID;
    private CubeMap cubeMap;
    private Vector3f position;
    private SceneData sceneData;

    public ReflectionProbe(Vector3f position, SceneData sceneData){
        this.position = position;
        this.sceneData = sceneData;
        this.cubeMap = new CubeMap();
    }

    public void update(){
        int cubeWidth = 512;
        int cubeHeight = 512;

        Matrix4f[] views = new Matrix4f[]{
                new Matrix4f().lookAt(position, new Vector3f(position.x + 1.0f, position.y + 0.0f, position.z + 0.0f), new Vector3f(0.0f, -1.0f, 0.0f)),
                new Matrix4f().lookAt(position, new Vector3f(position.x + -1.0f, position.y + 0.0f, position.z + 0.0f), new Vector3f(0.0f, -1.0f, 0.0f)),
                new Matrix4f().lookAt(position, new Vector3f(position.x + 0.0f, position.y +  1.0f, position.z + 0.0f), new Vector3f(0.0f, 0.0f, 1.0f)),
                new Matrix4f().lookAt(position, new Vector3f(position.x + 0.0f, position.y + -1.0f, position.z + 0.0f), new Vector3f(0.0f, 0.0f, -1.0f)),
                new Matrix4f().lookAt(position, new Vector3f(position.x + 0.0f, position.y + 0.0f, position.z + 1.0f), new Vector3f(0.0f, -1.0f, 0.0f)),
                new Matrix4f().lookAt(position, new Vector3f(position.x + 0.0f, position.y + 0.0f, position.z + -1.0f), new Vector3f(0.0f, -1.0f, 0.0f))
        };

        int FBO = glGenFramebuffers();

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

        if (!Shaders.skyboxProgram.isCompiled) Shaders.skyboxProgram.compile();
        if(!Shaders.environmentMappingProgram.isCompiled) Shaders.environmentMappingProgram.compile();
        glViewport(0, 0, cubeWidth, cubeHeight);
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
        for (int i = 0; i < 6; i++) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, environmentID, 0);
            glDisable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);

            Shaders.skyboxProgram.use();

            Shaders.skyboxProgram.uploadMat4f("cameraViewMatrix",CubeMap.views[i]);
            Shaders.skyboxProgram.uploadMat4f("cameraProjectionMatrix",CubeMap.projection);
            Shaders.skyboxProgram.uploadInt("equirectangularMap",0);
            sceneData.skybox.render();

            Shaders.environmentMappingProgram.use();
            glClear(GL_DEPTH_BUFFER_BIT);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);

            Shaders.environmentMappingProgram.uploadMat4f("cameraProjectionMatrix", CubeMap.projection);
            Shaders.environmentMappingProgram.uploadMat4f("cameraViewMatrix", views[i]);

            Shaders.environmentMappingProgram.uploadInt("albedo",T_ALBEDO);


            for(Model model : sceneData.staticModels){
                Shaders.environmentMappingProgram.uploadMat4f("modelMatrix",model.getModelMatrix());
                model.bindMaterial();
                model.render();
            }
        }

        Shaders.environmentMappingProgram.detach();
        glBindFramebuffer(GL_FRAMEBUFFER,0);

        cubeMap.setEnvironmentID(environmentID);
        cubeMap.generateMaps();
    }

    public void render(){
        glDrawArrays(GL_TRIANGLES,0,8);
    }

    public CubeMap getCubeMap(){return this.cubeMap;}

    public Vector3f getPosition(){return this.position;}
}
