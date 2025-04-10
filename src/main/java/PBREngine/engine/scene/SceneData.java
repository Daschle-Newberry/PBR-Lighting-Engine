package PBREngine.engine.scene;

import PBREngine.engine.scene.elements.Camera;
import PBREngine.engine.scene.elements.reflectionProbe.ReflectionProbeGrid;
import PBREngine.engine.scene.elements.emitters.DirectionalLight;
import PBREngine.engine.scene.elements.emitters.PointLight;
import PBREngine.engine.scene.elements.CubeMap;
import PBREngine.engine.scene.elements.model.Model;

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
