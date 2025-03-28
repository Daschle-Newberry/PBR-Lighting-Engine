package PBREngine.engine.gameobjects;


import PBREngine.engine.input.KeyListener;
import PBREngine.engine.scene.elements.Camera;
import org.joml.Vector3f;

public class Player {
    private static float speed = .005f;

    private Vector3f position;
    private Camera camera;


    public Player(Vector3f position, Camera camera){
        this.position = position;
        this.camera = camera;
    }
    public void updatePlayer(){
        movePlayer();
        camera.updateCameraPosition(position);


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

}

