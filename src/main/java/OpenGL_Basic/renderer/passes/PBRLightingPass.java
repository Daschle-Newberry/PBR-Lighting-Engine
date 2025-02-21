package OpenGL_Basic.renderer.passes;

import OpenGL_Basic.engine.Camera;
import OpenGL_Basic.engine.Emitters.DirectionalLight;
import OpenGL_Basic.engine.Emitters.PointLight;
import OpenGL_Basic.engine.Model;
import OpenGL_Basic.renderer.RenderPass;
import OpenGL_Basic.renderer.Renderer;
import OpenGL_Basic.renderer.Shaders;
import OpenGL_Basic.renderer.buffers.DepthBuffer;
import OpenGL_Basic.renderer.buffers.OutputBuffer;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class PBRLightingPass extends RenderPass {
    private Renderer renderer;
    private OutputBuffer outputBuffer;
    private DepthBuffer shadowMap;

    public PBRLightingPass(Renderer renderer, OutputBuffer outputBuffer, DepthBuffer shadowMap){
        this.renderer = renderer;
        this.outputBuffer = outputBuffer;
        this.shadowMap = shadowMap;
    }
    @Override
    public void render() {
        outputBuffer.bindToWrite();
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        glEnable(GL_CULL_FACE | GL_DEPTH_TEST);

//        shadowMap.bindToRead();

        ArrayList<Model> sceneObjects = renderer.renderQueue;
        PointLight[] pointLights = renderer.pointLights;
        DirectionalLight sceneLight = renderer.sceneLight;
        Camera sceneCamera = renderer.sceneCamera;

        /// MAIN PASS///

        Shaders.mainProgram.use();

        for(int i = 0; i < renderer.MAX_LIGHTS; i++){
            Shaders.mainProgram.uploadVec3f("lights[" + i +"].color",pointLights[i].getColor());
            Shaders.mainProgram.uploadFloat("lights[" + i +"].intensity",pointLights[i].getIntensity());
            Shaders.mainProgram.uploadVec3f("lights[" + i +"].positionDirection",pointLights[i].getPosition());
            Shaders.mainProgram.uploadInt("lights[" + i +"].isDirectional",0);

        }

        Shaders.mainProgram.uploadMat4f("lightViewMatrix",sceneLight.getViewMatrix());
//        Shaders.mainProgram.uploadMat4f("lightProjectionMatrix",shadowMap.getProjectionMatrix());
        Shaders.mainProgram.uploadVec3f("sun.direction",sceneLight.getLightFront());

        Shaders.mainProgram.uploadVec3f("cameraPos", sceneCamera.getCameraPosition());
        Shaders.mainProgram.uploadMat4f("cameraViewMatrix",sceneCamera.getViewMatrix());
        Shaders.mainProgram.uploadMat4f("cameraProjectionMatrix",sceneCamera.getProjectionMatrix());



//        Shaders.mainProgram.uploadVec2f("shadowMapDimensions",shadowMap.getMapDimensions());


        Shaders.mainProgram.uploadInt("albedo",0);
        Shaders.mainProgram.uploadInt("normalMap",1);
        Shaders.mainProgram.uploadInt("metallic",2);
        Shaders.mainProgram.uploadInt("roughness",3);
        Shaders.mainProgram.uploadInt("AO",4);
        Shaders.mainProgram.uploadInt("shadowMap",6);


        for(Model model : sceneObjects){
            model.bindMaterial();
            Shaders.mainProgram.uploadMat4f("modelMatrix",model.getModelMatrix());
            model.render();
        }


        Shaders.mainProgram.detach();
        glBindVertexArray(0);
    }
}
