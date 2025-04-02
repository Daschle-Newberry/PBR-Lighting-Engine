package PBREngine.engine.scene;

import PBREngine.engine.Window;
import PBREngine.engine.scene.elements.model.Model;
import PBREngine.engine.scene.elements.reflectionProbe.ReflectionProbeGrid;
import PBREngine.engine.gameobjects.GameObject;
import PBREngine.engine.scene.elements.emitters.DirectionalLight;
import PBREngine.engine.scene.elements.emitters.PointLight;
import PBREngine.engine.scene.elements.Camera;
import PBREngine.engine.gameobjects.Player;
import PBREngine.engine.input.MouseListener;
import PBREngine.engine.scene.elements.CubeMap;
import PBREngine.renderer.Renderer;
import PBREngine.renderer.Shaders;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;

public class MainScene extends Scene{
    //Scene Elements
    private int MAX_LIGHTS;
    private SceneData sceneData;
    //Gameplay Elements
    private Player player;
    private Renderer renderer;




    @Override
    public void init() {
        Shaders.loadShaders();
        sceneData = new SceneData();
        sceneData.models = new ArrayList<>();
        sceneData.staticModels = new ArrayList<>();
        sceneData.probeGrid = new ReflectionProbeGrid(new Vector3f(0.0f,.5f,0.0f), new Vector3f(.8f,.3f,.8f),3,sceneData);


        //Scene Elements
        MAX_LIGHTS = 4;
        sceneData.pointLights = new PointLight[MAX_LIGHTS];

        sceneData.pointLights[0] = new PointLight(new Vector3f(-1.0f,-1.0f,1.0f),2.0f,new Vector3f(1.0f,0.5f,0.5f));
        sceneData.pointLights[1] = new PointLight(new Vector3f(1.0f,-1.0f,1.0f),2.0f,new Vector3f(1.0f,0.5f,0.5f));
        sceneData.pointLights[2] = new PointLight(new Vector3f(-1.0f,1.0f,1.0f),2.0f,new Vector3f(1.0f,0.5f,0.5f));
        sceneData.pointLights[3] = new PointLight(new Vector3f(1.0f,1.0f,1.0f),2.0f,new Vector3f(1.0f,0.5f,0.5f));

        sceneData.sceneLight = new DirectionalLight( new Vector3f(1.0f,2.0f,2.0f),
                new Vector3f(0.0f,0.0f,0.0f),
                new Matrix4f().ortho(-4f,4f,-3,3,0.1f,10));

        sceneData.camera = new Camera(new Vector3f(0.0f,0.0f,0.0f));

        sceneData.skybox = new CubeMap("/assets/skybox/newport_loft.hdr");

        Model floor =  new  Model("/assets/models/floor.obj","/assets/materials/concrete");
        floor.setScale(.5f);
        floor.setEnvironmentMap(sceneData.skybox);
        sceneData.staticModels.add(floor);
        sceneData.models.add(floor);

        Model dragon =  new  Model("/assets/models/dragon.obj","/assets/materials/jade");
        dragon.setScale(.5f);
        dragon.setEnvironmentMap(sceneData.probeGrid.findNearestProbe(new Vector3f(0.0f,0.0f,0.0f)).getCubeMap());
        sceneData.models.add(dragon);

        Model sphere1 =  new  Model("/assets/models/sphere.obj","/assets/materials/veined_marble");
        sphere1.setScale(.5f);
        sphere1.setPosition(new Vector3f(-1.0f,0.0f,0.0f));
        sphere1.setEnvironmentMap(sceneData.probeGrid.findNearestProbe(new Vector3f(-1.0f,0.0f,0.0f)).getCubeMap());
        sceneData.models.add(sphere1);

        Model sphere2 =  new  Model("/assets/models/sphere.obj","/assets/materials/gold");
        sphere2.setScale(.5f);
        sphere2.setPosition(new Vector3f(1.0f,0.0f,0.0f));
        sphere2.setEnvironmentMap(sceneData.probeGrid.findNearestProbe(new Vector3f(1.0f,0.0f,0.0f)).getCubeMap());
        sceneData.models.add(sphere2);


        Model cube1 =  new  Model("/assets/models/cube.obj","/assets/materials/carbon_fiber");
        cube1.setScale(.5f);
        cube1.setPosition(new Vector3f(0.0f,.5f,-2.0f));
        cube1.setEnvironmentMap(sceneData.skybox);
        sceneData.staticModels.add(cube1);
        sceneData.models.add(cube1);

        Model cube2 =  new  Model("/assets/models/cube.obj","/assets/materials/plastic");
        cube2.setScale(.5f);
        cube2.setPosition(new Vector3f(1.4f,.5f,-1.5f));
        cube2.setRotation(45,new Vector3f(0.0f,1.0f,0.0f));
        cube2.setEnvironmentMap(sceneData.skybox);
        sceneData.staticModels.add(cube2);
        sceneData.models.add(cube2);


        Model cube3 =  new  Model("/assets/models/cube.obj","/assets/materials/plastic");
        cube3.setScale(.5f);
        cube3.setPosition(new Vector3f(-1.4f,.5f,-1.5f));
        cube3.setRotation(-45,new Vector3f(0.0f,1.0f,0.0f));
        cube3.setEnvironmentMap(sceneData.skybox);
        sceneData.staticModels.add(cube3);
        sceneData.models.add(cube3);

        sceneData.probeGrid.update();

        //Gameplay Elements
        player = new Player(new Vector3f(0,0,0),sceneData.camera);

        //Render Elements
        this.renderer = new Renderer(sceneData);
    }

    @Override
    public void update(double dt) {
        sceneData.camera.processMouseMovement(MouseListener.getDx(),MouseListener.getDy());
        MouseListener.proccessMovement();
        player.update();
        render();
    }



    @Override
    public void render() {
        renderer.start();

    }
}
