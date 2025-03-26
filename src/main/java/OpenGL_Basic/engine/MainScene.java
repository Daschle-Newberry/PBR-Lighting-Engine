package OpenGL_Basic.engine;

import OpenGL_Basic.engine.gameobjects.GameObject;
import OpenGL_Basic.engine.gameobjects.emitters.DirectionalLight;
import OpenGL_Basic.engine.gameobjects.emitters.PointLight;
import OpenGL_Basic.engine.gameobjects.Camera;
import OpenGL_Basic.engine.gameobjects.Player;
import OpenGL_Basic.engine.input.MouseListener;
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
        //Scene Elements
        MAX_LIGHTS = 4;
        sceneData.pointLights = new PointLight[MAX_LIGHTS];

        sceneData.pointLights[0] = new PointLight(new Vector3f(-1.0f,-1.0f,1.0f),2.0f,new Vector3f(1.0f,0.5f,0.5f));
        sceneData.pointLights[1] = new PointLight(new Vector3f(1.0f,-1.0f,1.0f),2.0f,new Vector3f(1.0f,0.5f,0.5f));
        sceneData.pointLights[2] = new PointLight(new Vector3f(-1.0f,1.0f,1.0f),2.0f,new Vector3f(1.0f,0.5f,0.5f));
        sceneData.pointLights[3] = new PointLight(new Vector3f(1.0f,1.0f,1.0f),2.0f,new Vector3f(1.0f,0.5f,0.5f));

        sceneData.sceneLight = new DirectionalLight( new Vector3f(0.0f,1.0f,5.0f),
                new Vector3f(0.0f,0.0f,-1.0f),
                new Matrix4f().ortho(-2,2,-2,2,0.1f,10));

        sceneData.camera = new Camera(new Vector3f(0.0f,0.0f,0.0f));
        gameObjects.add(sceneData.camera);


        Material[] materials =  loadMaterials(new String[]{"gold","sand"});
        ArrayList<Model> models =  new ArrayList<>();

        Model model = new Model("/assets/models/dragon.obj",materials[0]);
        model.setScale(.001f);
        model.setPosition(new Vector3f(0.0f,0.0f,0.0f));
        sceneData.models.add(model);

        Model room = new Model("/assets/models/room.obj",materials[1]);
        room.setPosition(new Vector3f(0.0f,-.5f,0.0f));
        sceneData.models.add(room);
        sceneData.staticModels.add(room);

        sceneData.skybox = new CubeMap("/assets/skybox/newport_loft.hdr");

        sceneData.probeGrid = new ReflectionProbeGrid(.2f,1,sceneData);

        //Gameplay Elements
        player = new Player(new Vector3f(0,0,0),sceneData.camera);

        //Render Elements
        this.renderer = new Renderer(sceneData);
    }

    private Material[] loadMaterials(String[] materialNames){

        Material[] materials = new Material[materialNames.length];
        for(int i = 0; i < materialNames.length; i ++){
            String format = FileReader.readLine(0,"/assets/materials/" + materialNames[i] + "/tags");
            String imageType = FileReader.readLine(1,"/assets/materials/" + materialNames[i] + "/tags");

            Material material;
            if(format.equals("ORM")){
                material = new Material("/assets/materials/" + materialNames[i] + "/albedo." + imageType,
                                          "/assets/materials/" + materialNames[i] + "/normal." + imageType,
                                            "/assets/materials/" + materialNames[i] + "/ORM." + imageType,
                                            "/assets/materials/" + materialNames[i] + "/ao." + imageType);
            }else{
                material = new Material("/assets/materials/" + materialNames[i] + "/albedo." + imageType,
                                          "/assets/materials/" + materialNames[i] + "/normal." + imageType,
                                         "/assets/materials/" + materialNames[i] + "/metallic." + imageType,
                                       "/assets/materials/" + materialNames[i] + "/roughness." + imageType,
                                             "/assets/materials/" + materialNames[i] + "/ao." + imageType);
            }

            materials[i] = material;
        }

        return  materials;
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
