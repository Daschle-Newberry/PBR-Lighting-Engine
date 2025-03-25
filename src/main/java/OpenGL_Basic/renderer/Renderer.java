package OpenGL_Basic.renderer;

import OpenGL_Basic.engine.ReflectionProbeGrid;
import OpenGL_Basic.engine.gameobjects.Camera;
import OpenGL_Basic.engine.CubeMap;
import OpenGL_Basic.engine.gameobjects.emitters.DirectionalLight;
import OpenGL_Basic.engine.gameobjects.emitters.PointLight;
import OpenGL_Basic.engine.Model;
import OpenGL_Basic.renderer.buffers.Texture;
import OpenGL_Basic.renderer.passes.*;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    public static final int B_NONE = -1;
    public static final int T_ALBEDO = 0;
    public static final int T_NORMAL = 1;
    public static final int T_AO = 2;
    public static final int T_METALLICORM = 3;
    public static final int T_ROUGHNESS = 4;

    public static final int B_IRRADIANCE_MAP = 5;
    public static final int B_SPECULAR_MAP = 6;
    public static final int B_BRDFLUT = 7;
    public static final int B_COLORTEX0 = 8;
    public static final int B_COLORTEX1 = 9;
    public static final int B_COLORTEX2 = 10;
    public static final int B_COLORTEX3 = 11;
    public static final int B_COLORTEX4 = 12;
    public static final int B_COLORTEX5 = 13;
    public static final int B_DEPTHTEX0 = 14;
    public static final int B_DEPTHTEX1 = 15;
    public static final int B_DEPTHTEX2 = 16;
    public static final int B_SHADOWMAP = 17;


    public ArrayList<Model> models;
    public PointLight[] pointLights;
    public DirectionalLight sceneLight;
    public Camera sceneCamera;
    public CubeMap skybox;
    public ReflectionProbeGrid probeGrid;

    public Map<Integer, Texture> buffers = new HashMap<>();

    private LinkedList<RenderPass> renderPasses =  new LinkedList<>();
    public Renderer(ArrayList<Model> models, PointLight[] pointLights, DirectionalLight sceneLight, Camera sceneCamera, CubeMap skyBox, ReflectionProbeGrid probeGrid) {
        this.models = models;
        this.pointLights = pointLights;
        this.sceneLight = sceneLight;
        this.sceneCamera = sceneCamera;
        this.skybox = skyBox;
        this.probeGrid = probeGrid;


        addPasses();


    }
    private void addPasses() {
        renderPasses.add(new DepthPass(this,sceneLight,B_SHADOWMAP));
        renderPasses.add(new SkyboxPass(this,new int[]{B_COLORTEX0},B_NONE));
        renderPasses.add(new ProbePass(this,new int[]{B_COLORTEX0},B_NONE));
//        renderPasses.add(new PBRLightingPass(this,new int[]{B_COLORTEX0},B_DEPTHTEX0));
        renderPasses.add(new FinalPass(this,new int[]{B_COLORTEX0},B_NONE));

    }
    public Texture getBuffer(int name) {
        Texture uniform = buffers.get(name);
        if(uniform == null) throw new RuntimeException("Unknown uniform " + name);
        return uniform;
    }

    public Texture ensureColorBuffer(int type){
        Texture buffer = buffers.computeIfAbsent(type, _ -> new Texture(GL_RGBA16, GL_RGBA,2560,1440,type));
        return buffer;
    }
    public Texture ensureDepthBuffer(int type){
        Texture buffer = buffers.computeIfAbsent(type, _ ->new Texture(GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT,2560,1440,type));
        return buffer;
    }
    public void start(){
        for(RenderPass pass : renderPasses){
            pass.render();
        }
    }


}
