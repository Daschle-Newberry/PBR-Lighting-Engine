package PBREngine.renderer;

import PBREngine.engine.Window;
import PBREngine.engine.scene.SceneData;
import PBREngine.renderer.buffers.BRDFLUT;
import PBREngine.renderer.buffers.Sampler2D;
import PBREngine.renderer.buffers.Texture;
import PBREngine.renderer.passes.*;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

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



    public SceneData sceneData;

    public Map<Integer, Sampler2D> buffers = new HashMap<>();
    private LinkedList<RenderPass> renderPasses =  new LinkedList<>();

    private boolean pbrON = false;

    public Renderer(SceneData sceneData) {
        this.sceneData = sceneData;
        addPassesShaded();
    }

    public void resize(){
        buffers = new HashMap<>();
        for(RenderPass p : renderPasses){
            p.resizeFramebuffers(this);
        }
    }

    public void swapRenderType(boolean isToggled){
        if(isToggled && !pbrON){
            addPassesShaded();
            pbrON = true;
        }
        else if(!isToggled && pbrON){
            addPassesDebug();
            pbrON = false;
        }
    }
    public void addPassesDebug() {
        renderPasses = new LinkedList<>();
        renderPasses.add(new DepthPass(this,sceneData, sceneData.sceneLight,B_SHADOWMAP));
        renderPasses.add(new SkyboxPass(this,sceneData,new int[]{B_COLORTEX0},B_NONE));
        renderPasses.add(new DebugLightingPass(this,sceneData,new int[]{B_COLORTEX0},B_DEPTHTEX0));
        renderPasses.add(new FinalPass(this,new int[]{B_COLORTEX0},B_NONE));
    }
    public void addPassesShaded(){
        renderPasses = new LinkedList<>();
        renderPasses.add(new DepthPass(this,sceneData, sceneData.sceneLight,B_SHADOWMAP));
        renderPasses.add(new SkyboxPass(this,sceneData,new int[]{B_COLORTEX0},B_NONE));
        renderPasses.add(new PBRLightingPass(this,sceneData,new int[]{B_COLORTEX0},B_DEPTHTEX0));
        renderPasses.add(new FinalPass(this,new int[]{B_COLORTEX0},B_NONE));
    }
    public Sampler2D getBuffer(int name) {
        Sampler2D uniform = buffers.get(name);
        if(uniform == null) throw new RuntimeException("Unknown uniform " + name);
        return uniform;
    }

    public Sampler2D ensureColorBuffer(int type){
        Sampler2D buffer = buffers.computeIfAbsent(type, _ -> new Texture(GL_RGBA16F, GL_RGBA, Window.get().width,Window.get().height, type));
        return buffer;
    }
    public Sampler2D ensureDepthBuffer(int type){
        Sampler2D buffer = buffers.computeIfAbsent(type, _ ->new Texture(GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT,Window.get().width,Window.get().height,type));
        return buffer;
    }

    public void createBRDFLUT(){
        Sampler2D brdfLUT = buffers.computeIfAbsent(B_BRDFLUT, _ -> new BRDFLUT());
    }
    public void start(){
        for(RenderPass pass : renderPasses){
            pass.render();
        }
    }


}
