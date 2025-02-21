package OpenGL_Basic.renderer;

import OpenGL_Basic.engine.Camera;
import OpenGL_Basic.engine.Emitters.DirectionalLight;
import OpenGL_Basic.engine.Emitters.PointLight;
import OpenGL_Basic.engine.Model;
import OpenGL_Basic.renderer.buffers.DepthBuffer;
import OpenGL_Basic.renderer.buffers.OutputBuffer;
import OpenGL_Basic.renderer.buffers.ScreenBuffer;
import OpenGL_Basic.renderer.passes.FinalPass;
import OpenGL_Basic.renderer.passes.PBRLightingPass;

import java.util.ArrayList;

public class Renderer {
    public int MAX_LIGHTS = 4;
    public ArrayList<RenderPass> renderPasses;
    public ArrayList<Model> renderQueue;
    public PointLight[] pointLights;
    public DirectionalLight sceneLight;
    public Camera sceneCamera;

    private ScreenBuffer renderOutput;
    private DepthBuffer shadowMap,depthMap;

    private RenderPass lightingPass, screenPass;

    public Renderer( ArrayList<Model> renderQueue, PointLight[] pointLights, DirectionalLight sceneLight, Camera sceneCamera ){
        this.renderQueue = renderQueue;
        this.pointLights = pointLights;
        this.sceneLight = sceneLight;
        this.sceneCamera = sceneCamera;

        this.renderOutput = new ScreenBuffer();

        lightingPass = new PBRLightingPass(this,renderOutput,shadowMap);
        screenPass =  new FinalPass(renderOutput);
    }

    public <T extends RenderPass> void addRenderPass(RenderPass pass){
        renderPasses.add(pass);
    }

    public void start(){
        lightingPass.render();
        screenPass.render();
    }


}
