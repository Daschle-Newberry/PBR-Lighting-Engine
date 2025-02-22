package OpenGL_Basic.renderer;

import OpenGL_Basic.engine.Camera;
import OpenGL_Basic.engine.CubeMap;
import OpenGL_Basic.engine.Emitters.DirectionalLight;
import OpenGL_Basic.engine.Emitters.PointLight;
import OpenGL_Basic.engine.Model;
import OpenGL_Basic.renderer.buffers.DepthBuffer;
import OpenGL_Basic.renderer.buffers.ColorBuffer;
import OpenGL_Basic.renderer.buffers.OutputBuffer;
import OpenGL_Basic.renderer.passes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    public ArrayList<Model> models;
    public PointLight[] pointLights;
    public DirectionalLight sceneLight;
    public Camera sceneCamera;
    public CubeMap skybox;


    public HashMap<String, OutputBuffer> outputs = new HashMap<>();
    private LinkedList<RenderPass> renderPasses =  new LinkedList<>();


    public Renderer(ArrayList<Model> models, PointLight[] pointLights, DirectionalLight sceneLight, Camera sceneCamera, CubeMap skyBox){
        this.models = models;
        this.pointLights = pointLights;
        this.sceneLight = sceneLight;
        this.sceneCamera = sceneCamera;
        this.skybox = skyBox;


        outputs.put("RenderOutput",new ColorBuffer());

        addPass(new SkyboxPass(this));
        addPass(new PBRLightingPass(this));


        renderPasses.add(new FinalPass(this));
        Shaders.loadShaders();
    }

    public void addPass(RenderPass pass){
        String[] dependencies = pass.getDependencies();
        if (dependencies != null){
            for(String dependency : dependencies) {
                addDependency(dependency);
            }
            pass.sourceDependencies();
        }
        renderPasses.add(pass);
    }

    private void addDependency(String dependency){
        if(dependency.equals("CameraDepth")){
            RenderPass pass = new DepthPass(this,sceneCamera);
            addPass(pass);
            outputs.put("CameraDepth",pass.getBuffer());

        }else if(dependency.equals("SceneLightDepth")){
            RenderPass pass = new DepthPass(this,sceneLight);
            addPass(pass);
            outputs.put("SceneLightDepth",pass.getBuffer());
        }
    }
    public void start(){

       for(RenderPass pass : renderPasses){
           pass.render();
       }
    }


}
