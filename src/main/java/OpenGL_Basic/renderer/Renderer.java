package OpenGL_Basic.renderer;

import OpenGL_Basic.engine.Camera;
import OpenGL_Basic.engine.CubeMap;
import OpenGL_Basic.engine.Emitters.DirectionalLight;
import OpenGL_Basic.engine.Emitters.PointLight;
import OpenGL_Basic.engine.Model;
import OpenGL_Basic.renderer.buffers.RenderTarget;
import OpenGL_Basic.renderer.buffers.Buffer;
import OpenGL_Basic.renderer.passes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Renderer {
    public static final int R_OUTPUT_BUFFER = 0;
    public static final int R_CAMERA_DEPTH_BUFFER = 1;
    public static final int R_SCENELIGHT_DEPTH_BUFFER = 2;
    public static final int T_ALBEDO = 0;
    public static final int T_NORMAL = 1;
    public static final int T_METALLIC = 2;
    public static final int T_ROUGHNESS = 3;
    public static final int T_AO = 4;

    public ArrayList<Model> models;
    public PointLight[] pointLights;
    public DirectionalLight sceneLight;
    public Camera sceneCamera;
    public CubeMap skybox;


    public HashMap<String, Object> data = new HashMap<>();

    private LinkedList<RenderPass> renderPasses =  new LinkedList<>();
    private DebugLightingPass debugPass;
    private RenderPass pass;
    public Renderer(ArrayList<Model> models, PointLight[] pointLights, DirectionalLight sceneLight, Camera sceneCamera, CubeMap skyBox){
        Shaders.loadShaders();
        this.models = models;
        this.pointLights = pointLights;
        this.sceneLight = sceneLight;
        this.sceneCamera = sceneCamera;
        this.skybox = skyBox;

        this.pass = new DebugLightingPass(this);

        data.put("cameraViewMatrix",sceneCamera.getViewMatrix());
        data.put("cameraProjectionMatrix",sceneCamera.getProjectionMatrix());

    }

    public Object getData(String name) {
        Object uniform = data.get(name);

        if (uniform == null) throw new RuntimeException("Unknown uniform " + name);

        return uniform;
    }
    public void start(){
        pass.render();
    }


}
//public void addPass(RenderPass pass){
//    int[] dependencies = pass.getDependencies();
//    if (dependencies != null){
//        for(int dependency : dependencies) {
//            addDependency(dependency);
//        }
//        pass.sourceDependencies();
//    }
//    renderPasses.add(pass);
//}

//private void addDependency(int dependency){
//    if(dependency == R_CAMERA_DEPTH_BUFFER){
//        RenderPass pass = new DepthPass(this,sceneCamera);
//        addPass(pass);
//        outputs.put(R_CAMERA_DEPTH_BUFFER,pass.getBuffer());
//
//    }else if(dependency == R_SCENELIGHT_DEPTH_BUFFER){
//        RenderPass pass = new DepthPass(this,sceneLight);
//        addPass(pass);
//        outputs.put(R_SCENELIGHT_DEPTH_BUFFER,pass.getBuffer());
//    }
//}