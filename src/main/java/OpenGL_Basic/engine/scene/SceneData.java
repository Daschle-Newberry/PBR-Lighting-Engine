package OpenGL_Basic.engine.scene;

import OpenGL_Basic.engine.scene.elements.reflectionProbe.ReflectionProbeGrid;
import OpenGL_Basic.engine.scene.elements.Camera;
import OpenGL_Basic.engine.scene.elements.emitters.DirectionalLight;
import OpenGL_Basic.engine.scene.elements.emitters.PointLight;
import OpenGL_Basic.engine.scene.elements.CubeMap;
import OpenGL_Basic.engine.scene.elements.model.Model;

import java.util.ArrayList;

public class SceneData {
    public Camera camera;
    public DirectionalLight sceneLight;
    public PointLight pointLights[];
    public ArrayList<Model> staticModels;
    public ArrayList<Model> models;
    public CubeMap skybox;
    public ReflectionProbeGrid probeGrid;
}
