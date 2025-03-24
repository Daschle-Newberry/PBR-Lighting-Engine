package OpenGL_Basic.renderer.passes;

import OpenGL_Basic.engine.Model;
import OpenGL_Basic.engine.Window;
import OpenGL_Basic.engine.gameobjects.emitters.PointLight;
import OpenGL_Basic.renderer.Renderer;
import OpenGL_Basic.renderer.Shader;
import OpenGL_Basic.renderer.Shaders;
import OpenGL_Basic.renderer.buffers.Buffer;
import OpenGL_Basic.renderer.buffers.FrameBuffer;
import OpenGL_Basic.renderer.buffers.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.windows.POINT;

import static OpenGL_Basic.renderer.Renderer.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class PBRLightingPass extends RenderPass {
    private static Shader shader = Shaders.PBRProgram;
    private Renderer renderer;
    private FrameBuffer FBO;

    public PBRLightingPass(Renderer renderer, int[] colorBufferRequest, int depthBufferRequest) {
        if(!shader.isCompiled) shader.compile();
        this.renderer = renderer;
        FBO = createFrameBuffer(colorBufferRequest,depthBufferRequest,renderer);
    }

    @Override
    public void render() {
        FBO.bindToWrite();
        shader.use();
        glClear(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        shader.uploadMat4f("cameraProjectionMatrix", renderer.sceneCamera.getProjectionMatrix());
        shader.uploadMat4f("cameraViewMatrix", renderer.sceneCamera.getViewMatrix());
        shader.uploadVec3f("cameraPosition",renderer.sceneCamera.getCameraPosition());

        shader.uploadMat4f("lightProjectionMatrix",renderer.sceneLight.getProjectionMatrix());
        shader.uploadMat4f("lightViewMatrix",renderer.sceneLight.getViewMatrix());
        shader.uploadVec3f("sun.direction",renderer.sceneLight.getLightFront());

        shader.uploadInt("albedo",T_ALBEDO);
        shader.uploadInt("normalMap",T_NORMAL);
        shader.uploadInt("metallicORM",T_METALLICORM);
        shader.uploadInt("roughness",T_ROUGHNESS);
        shader.uploadInt("AO",T_AO);


        shader.uploadVec3f("lights[" + 0 + "].color", renderer.sceneLight.getColor());
        shader.uploadVec3f("lights[" + 0 + "].positionDirection", renderer.sceneLight.getLightFront());
        shader.uploadFloat("lights[" + 0 + "].intensity", renderer.sceneLight.getIntensity());
        shader.uploadInt("lights[" + 0 + "].isDirectional", 1);

        for (int i = 1; i < renderer.pointLights.length; i++) {
            shader.uploadVec3f("lights[" + i + "].color", renderer.pointLights[i].getColor());
            shader.uploadVec3f("lights[" + i + "].positionDirection", renderer.pointLights[i].getPosition());
            shader.uploadFloat("lights[" + i + "].intensity", renderer.pointLights[i].getIntensity());
            shader.uploadInt("lights[" + i + "].isDirectional", 0);
        }

        shader.uploadInt("irradianceMap",B_IRRADIANCE_MAP);
        shader.uploadInt("specularMap",B_SPECULAR_MAP);
        shader.uploadInt("brdfLUT",B_BRDFLUT);
        renderer.skybox.bindIrradianceMap();
        renderer.skybox.bindSpecularMap();
        renderer.skybox.bindBRDFLUT();

        Texture shadowMap = renderer.getBuffer(B_SHADOWMAP);
        shader.uploadInt("shadowMap",B_SHADOWMAP);
        shader.uploadVec2f("shadowMapDimensions", shadowMap.getDimensions());
        shadowMap.bind();


        for(Model model : renderer.models){
            shader.uploadMat4f("modelMatrix",model.getModelMatrix());
            shader.uploadInt("useORM",model.getMaterialType());
            model.bindMaterial();
            model.render();
        }


        FBO.detach();
        shader.detach();
        glBindVertexArray(0);
    }
}
