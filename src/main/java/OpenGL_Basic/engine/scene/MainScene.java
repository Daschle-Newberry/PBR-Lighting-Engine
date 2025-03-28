package OpenGL_Basic.engine.scene;

import OpenGL_Basic.engine.scene.elements.model.Material;
import OpenGL_Basic.engine.scene.elements.model.Model;
import OpenGL_Basic.engine.scene.elements.reflectionProbe.ReflectionProbeGrid;
import OpenGL_Basic.engine.gameobjects.GameObject;
import OpenGL_Basic.engine.scene.elements.emitters.DirectionalLight;
import OpenGL_Basic.engine.scene.elements.emitters.PointLight;
import OpenGL_Basic.engine.scene.elements.Camera;
import OpenGL_Basic.engine.gameobjects.Player;
import OpenGL_Basic.engine.input.MouseListener;
import OpenGL_Basic.engine.scene.elements.CubeMap;
import OpenGL_Basic.renderer.Renderer;
import OpenGL_Basic.renderer.Shaders;
import OpenGL_Basic.util.FileReader;
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
    private ArrayList<GameObject> gameObjects;




    @Override
    public void init() {
        Shaders.loadShaders();
        sceneData = new SceneData();
        gameObjects = new ArrayList<>();
        sceneData.models = new ArrayList<>();
        sceneData.staticModels = new ArrayList<>();
        sceneData.probeGrid = new ReflectionProbeGrid(new Vector3f(0.0f,0.0f,0.0f), new Vector3f(.8f,.3f,.8f),1,sceneData);


        //Scene Elements
        MAX_LIGHTS = 4;
        sceneData.pointLights = new PointLight[MAX_LIGHTS];

        sceneData.pointLights[0] = new PointLight(new Vector3f(-1.0f,-1.0f,1.0f),2.0f,new Vector3f(1.0f,0.5f,0.5f));
        sceneData.pointLights[1] = new PointLight(new Vector3f(1.0f,-1.0f,1.0f),2.0f,new Vector3f(1.0f,0.5f,0.5f));
        sceneData.pointLights[2] = new PointLight(new Vector3f(-1.0f,1.0f,1.0f),2.0f,new Vector3f(1.0f,0.5f,0.5f));
        sceneData.pointLights[3] = new PointLight(new Vector3f(1.0f,1.0f,1.0f),2.0f,new Vector3f(1.0f,0.5f,0.5f));

        sceneData.sceneLight = new DirectionalLight( new Vector3f(0.0f,2.0f,2.0f),
                new Vector3f(0.0f,-.6f,-1.0f),
                new Matrix4f().ortho(-2,2,-2,2,0.1f,10));

        sceneData.camera = new Camera(new Vector3f(0.0f,0.0f,0.0f));
        gameObjects.add(sceneData.camera);

        sceneData.skybox = new CubeMap("/assets/skybox/newport_loft.hdr");

//        Model room = new Model("/assets/models/room.obj","/assets/materials/sand");
//        room.setPosition(new Vector3f(0.0f,0.0f,0.0f));
//        room.setEnvironmentMap(sceneData.skybox);
//        sceneData.models.add(room);
//        sceneData.staticModels.add(room);

        Model model = new Model("/assets/models/cube.obj","/assets/materials/gold");
        model.setScale(.25f);
        model.setPosition(new Vector3f(.5f,0.0f,0.0f));
        model.setEnvironmentMap(sceneData.skybox);
        sceneData.staticModels.add(model);
        sceneData.models.add(model);

        Model model2 = new Model("/assets/models/cube.obj","/assets/materials/gold");
        model2.setScale(.25f);
        model2.setPosition(new Vector3f(1.5f,0.0f,0.0f));
        model2.setEnvironmentMap(sceneData.skybox);
        sceneData.staticModels.add(model2);
        sceneData.models.add(model2);

        Model model3 = new Model("/assets/models/cube.obj","/assets/materials/jade");
        model3.setScale(.25f);
        model3.setPosition(new Vector3f(1.0f,0.5f,0.0f));
        model3.setEnvironmentMap(sceneData.skybox);
        sceneData.staticModels.add(model3);
        sceneData.models.add(model3);

        Model model4 = new Model("/assets/models/cube.obj","/assets/materials/jade");
        model4.setScale(.25f);
        model4.setPosition(new Vector3f(1.0f,-0.5f,0.0f));
        model4.setEnvironmentMap(sceneData.skybox);
        sceneData.staticModels.add(model4);
        sceneData.models.add(model4);

//        Model model2 = new Model("/assets/models/sphere.obj","/assets/materials/plastic");
//        model2.setScale(.25f);
//        model2.setPosition(new Vector3f(-.5f,0.0f,0.0f));
//        model2.setEnvironmentMap(sceneData.probeGrid.findNearestProbe(new Vector3f(-1.0f,0.0f,0.0f)).getCubeMap());
//        sceneData.models.add(model2);
//
//        Model model3 = new Model("/assets/models/sphere.obj","/assets/materials/jade");
//        model3.setScale(.25f);
//        model3.setPosition(new Vector3f(.5f,0.0f,0.0f));
//        model3.setEnvironmentMap(sceneData.probeGrid.findNearestProbe(new Vector3f(-1.0f,0.0f,0.0f)).getCubeMap());
//        sceneData.models.add(model3);


        sceneData.probeGrid.update();

        //Gameplay Elements
        player = new Player(new Vector3f(0,0,0),sceneData.camera);

        //Render Elements
        this.renderer = new Renderer(sceneData);
    }

    @Override
    public void update(double dt) {
        for(GameObject g : gameObjects){
            g.update();
        }
        sceneData.camera.processMouseMovement(MouseListener.getDx(),MouseListener.getDy());
        MouseListener.proccessMovement();
        player.updatePlayer();
        render();
    }



    @Override
    public void render() {
        renderer.start();

    }
}
