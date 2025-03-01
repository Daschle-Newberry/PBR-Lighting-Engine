package OpenGL_Basic.engine;

import OpenGL_Basic.engine.Emitters.DirectionalLight;
import OpenGL_Basic.engine.Emitters.PointLight;
import OpenGL_Basic.engine.input.MouseListener;
import OpenGL_Basic.renderer.Renderer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;

public class GameScene extends Scene{
    //Scene Elements
    private int MAX_LIGHTS;
    private PointLight pointLights[];
    private DirectionalLight sun;

    private Model mainModel,room;
    private Material[] materials;
    private Camera camera;

    private CubeMap skybox;

    //Gameplay Elements
    private Player player;
    private Renderer renderer;




    @Override
    public void init() {

        //Scene Elements
        MAX_LIGHTS = 4;
        pointLights = new PointLight[MAX_LIGHTS];

        pointLights[0] = new PointLight(new Vector3f(-1.0f,-1.0f,1.0f),2.0f,new Vector3f(1.0f,1.0f,1.0f));
        pointLights[1] = new PointLight(new Vector3f(1.0f,-1.0f,1.0f),2.0f,new Vector3f(1.0f,1.0f,1.0f));
        pointLights[2] = new PointLight(new Vector3f(-1.0f,1.0f,1.0f),2.0f,new Vector3f(1.0f,1.0f,1.0f));
        pointLights[3] = new PointLight(new Vector3f(1.0f,1.0f,1.0f),2.0f,new Vector3f(1.0f,1.0f,1.0f));

        sun = new DirectionalLight( new Vector3f(0.0f,10,5.0f),
                                    new Vector3f(0.0f,0.0f,1.0f),
                                    new Matrix4f().ortho(-10,10,-10,10,0.1f,10));



        materials =  loadMaterials(new String[]{"sand","rusted_iron"});

        ArrayList<Model> models =  new ArrayList<>();

        mainModel = new Model("/assets/models/sphere.obj",materials[0]);
        mainModel.setScale(.1f);
        mainModel.setPosition(new Vector3f(0.0f,2.0f,0.0f));

        models.add(mainModel);

        camera = new Camera(new Vector3f(0.0f,0.0f,0.0f));

        skybox =  new CubeMap(new String[]{
                        "/assets/skybox/right.jpg",
                        "/assets/skybox/left.jpg",
                        "/assets/skybox/top.jpg",
                        "/assets/skybox/bottom.jpg",
                        "/assets/skybox/front.jpg",
                        "/assets/skybox/back.jpg"});

        //Gameplay Elements
        player = new Player(new Vector3f(0,0,0),camera);

        //Render Elements
        this.renderer = new Renderer(models,pointLights,sun,camera,skybox);

    }

    private Material[] loadMaterials(String[] materialnames){
        Material[] materials = new Material[materialnames.length];
        for (int i = 0; i < materialnames.length; i++) {
            String name = materialnames[i];
            materials[i] = new Material(
                    "/assets/materials/" + name + "/albedo.png",
                    "/assets/materials/" + name + "/normal.png",
                    "/assets/materials/" + name + "/metallic.png",
                    "/assets/materials/" + name + "/roughness.png",
                    "/assets/materials/" + name + "/ao.png"
            );
        }

        return  materials;
    }


    @Override
    public void update(double dt) {
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
