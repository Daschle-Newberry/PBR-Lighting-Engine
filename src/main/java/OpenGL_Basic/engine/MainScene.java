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
    private PointLight pointLights[];

    private ArrayList<GameObject> gameObjects;
    private DirectionalLight sun;
    private Model mainModel,room;
    private Material[] materials;
    private Camera camera;

    private CubeMap skybox;
    private ReflectionProbeGrid probeGrid;

    //Gameplay Elements
    private Player player;
    private Renderer renderer;




    @Override
    public void init() {
        Shaders.loadShaders();
        gameObjects = new ArrayList<>();
        //Scene Elements
        MAX_LIGHTS = 4;
        pointLights = new PointLight[MAX_LIGHTS];

        pointLights[0] = new PointLight(new Vector3f(-1.0f,-1.0f,1.0f),2.0f,new Vector3f(1.0f,0.5f,0.5f));
        pointLights[1] = new PointLight(new Vector3f(1.0f,-1.0f,1.0f),2.0f,new Vector3f(1.0f,0.5f,0.5f));
        pointLights[2] = new PointLight(new Vector3f(-1.0f,1.0f,1.0f),2.0f,new Vector3f(1.0f,0.5f,0.5f));
        pointLights[3] = new PointLight(new Vector3f(1.0f,1.0f,1.0f),2.0f,new Vector3f(1.0f,0.5f,0.5f));

        sun = new DirectionalLight( new Vector3f(0.0f,1.0f,5.0f),
                new Vector3f(0.0f,0.0f,-1.0f),
                new Matrix4f().ortho(-2,2,-2,2,0.1f,10));

        camera = new Camera(new Vector3f(0.0f,0.0f,0.0f));
        gameObjects.add(camera);


        materials =  loadMaterials(new String[]{"plastic","grass","gold","rusted_iron"});
        ArrayList<Model> models =  new ArrayList<>();

        Model model = new Model("/assets/models/dragon.obj",materials[2]);
        model.setScale(.01f);
        model.setPosition(new Vector3f(0.0f,2.0f,0.0f));
        models.add(model);


        skybox =  new CubeMap("/assets/skybox/newport_loft.hdr");

        probeGrid = new ReflectionProbeGrid(.2f,1);

        //Gameplay Elements
        player = new Player(new Vector3f(0,0,0),camera);

        //Render Elements
        this.renderer = new Renderer(models,pointLights,sun,camera,skybox,probeGrid);
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
        camera.processMouseMovement(MouseListener.getDx(),MouseListener.getDy());
        MouseListener.proccessMovement();
        player.updatePlayer();
        render();
    }



    @Override
    public void render() {
        renderer.start();

    }
}
