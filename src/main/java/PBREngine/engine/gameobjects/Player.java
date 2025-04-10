package PBREngine.engine.gameobjects;


import PBREngine.engine.input.KeyListener;
import PBREngine.engine.scene.elements.Camera;
import org.joml.Vector3f;

public class Player implements GameObject{
    private static float speed = .005f;

    private Vector3f position;
    private Camera camera;
    private boolean firstPerson;


    public Player(Vector3f position, Camera camera, boolean firstPerson){
        this.position = position;
        this.camera = camera;
        this.firstPerson = firstPerson;
    }


    private void movePlayer(){
        Vector3f cameraFront = camera.getFrontVector();
        Vector3f cameraRight = camera.getRightVector();
        if (KeyListener.get().isKeyPressed(87)){
            position.x += cameraFront.x * speed;
            position.y += cameraFront.y * speed;
            position.z += cameraFront.z * speed;


        }
        if (KeyListener.get().isKeyPressed(83)){
            position.x -= cameraFront.x * speed;
            position.y -= cameraFront.y * speed;
            position.z -= cameraFront.z * speed;
        }
        if (KeyListener.get().isKeyPressed(68)){
            position.x -= cameraRight.x * speed;
            position.y -= cameraRight.y * speed;
            position.z -= cameraRight.z * speed;
        }
        if (KeyListener.get().isKeyPressed(65)){
            position.x += cameraRight.x * speed;
            position.y += cameraRight.y * speed;
            position.z += cameraRight.z * speed;
        }


    }

    public Vector3f getPosition(){
        return position;
    }

    @Override
    public void update() {
        if(firstPerson) movePlayer();
        camera.setPosition(position);
    }
}

