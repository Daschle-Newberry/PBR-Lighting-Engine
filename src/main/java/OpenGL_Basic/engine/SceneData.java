package OpenGL_Basic.engine;

import OpenGL_Basic.engine.gameobjects.Camera;
import OpenGL_Basic.engine.gameobjects.GameObject;
import OpenGL_Basic.engine.gameobjects.emitters.DirectionalLight;
import OpenGL_Basic.engine.gameobjects.emitters.PointLight;

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
