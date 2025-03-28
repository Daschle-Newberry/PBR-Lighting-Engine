package PBREngine.renderer.passes;

import PBREngine.engine.scene.elements.model.Model;
import PBREngine.engine.scene.SceneData;
import PBREngine.renderer.Renderer;
import PBREngine.renderer.Shader;
import PBREngine.renderer.Shaders;
import PBREngine.renderer.buffers.FrameBuffer;
import PBREngine.renderer.buffers.Sampler2D;

import static PBREngine.renderer.Renderer.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class PBRLightingPass extends RenderPass {
    private static Shader shader = Shaders.PBRProgram;
    private SceneData sceneData;
    private Renderer renderer;
    private FrameBuffer FBO;

    public PBRLightingPass(Renderer renderer, SceneData sceneData, int[] colorBufferRequest, int depthBufferRequest) {
        if(!shader.isCompiled) shader.compile();
        this.sceneData = sceneData;
        this.renderer = renderer;
        renderer.createBRDFLUT();
        FBO = createFrameBuffer(colorBufferRequest,depthBufferRequest,renderer);
    }

    @Override
    public void render() {
        FBO.bindToWrite();
        glClear(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        shader.use();
        shader.uploadMat4f("cameraProjectionMatrix", sceneData.camera.getProjectionMatrix());
        shader.uploadMat4f("cameraViewMatrix", sceneData.camera.getViewMatrix());
        shader.uploadVec3f("cameraPosition",sceneData.camera.getCameraPosition());

        shader.uploadMat4f("lightProjectionMatrix",sceneData.sceneLight.getProjectionMatrix());
        shader.uploadMat4f("lightViewMatrix",sceneData.sceneLight.getViewMatrix());
        shader.uploadVec3f("sun.direction",sceneData.sceneLight.getLightFront());

        shader.uploadInt("albedo",T_ALBEDO);
        shader.uploadInt("normalMap",T_NORMAL);
        shader.uploadInt("metallicORM",T_METALLICORM);
        shader.uploadInt("roughness",T_ROUGHNESS);
        shader.uploadInt("AO",T_AO);


        shader.uploadVec3f("lights[" + 0 + "].color", sceneData.sceneLight.getColor());
        shader.uploadVec3f("lights[" + 0 + "].positionDirection", sceneData.sceneLight.getLightFront());
        shader.uploadFloat("lights[" + 0 + "].intensity", sceneData.sceneLight.getIntensity());
        shader.uploadInt("lights[" + 0 + "].isDirectional", 1);

        for (int i = 1; i < sceneData.pointLights.length; i++) {
            shader.uploadVec3f("lights[" + i + "].color", sceneData.pointLights[i].getColor());
            shader.uploadVec3f("lights[" + i + "].positionDirection", sceneData.pointLights[i].getPosition());
            shader.uploadFloat("lights[" + i + "].intensity", sceneData.pointLights[i].getIntensity());
            shader.uploadInt("lights[" + i + "].isDirectional", 0);
        }

        shader.uploadInt("irradianceMap",B_IRRADIANCE_MAP);
        shader.uploadInt("specularMap",B_SPECULAR_MAP);
        shader.uploadInt("brdfLUT",B_BRDFLUT);
        renderer.getBuffer(B_BRDFLUT).bind();

        Sampler2D shadowMap = renderer.getBuffer(B_SHADOWMAP);
        shader.uploadInt("shadowMap",B_SHADOWMAP);
        shader.uploadVec2f("shadowMapDimensions", shadowMap.getDimensions());
        shadowMap.bind();

        for(Model model : sceneData.models){
            shader.uploadMat4f("modelMatrix",model.getModelMatrix());
            shader.uploadInt("useORM",model.getMaterialType());
            model.bindMaterial();
            model.bindEnvironmentMap();
            model.render();
        }

        FBO.detach();
        shader.detach();
        glBindVertexArray(0);
    }
}
