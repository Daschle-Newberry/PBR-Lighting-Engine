package PBREngine.engine.scene.elements;
import PBREngine.engine.Window;
import PBREngine.engine.gameobjects.GameObject;
import org.joml.Matrix4f;

import org.joml.Vector3f;

public class Camera implements Perspective {
    private static float sensitivity = .1f;

    private Matrix4f projectionMatrix,viewMatrix,viewMatrixNoTranslation;
    private float pitch,yaw = 0.0f;
    private Vector3f position;
    private Vector3f cameraFront = new Vector3f(0.0f,0f,-1f);
    private Vector3f cameraUp = new Vector3f(0.0f,1.0f,0.0f);


    public Camera(Vector3f position){
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        viewMatrixNoTranslation = new Matrix4f();
        adjustProjection();

    }
    public void updateCameraPosition(Vector3f newPosition){
        this.position = newPosition;
    }
    public void processMouseMovement(float dX, float dY){
        float xOffset = dX * this.sensitivity;
        float yOffset = dY  * this.sensitivity;

        this.yaw += xOffset;
        this.pitch += yOffset;

        if (this.pitch >= 89f) {
            this.pitch = 89f;

        }
        if (this.pitch <= -89f) {
            this.pitch = -89f;
        }

        float lookX = (float)(Math.cos(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch)));
        float lookY = (float)(Math.sin(Math.toRadians(this.pitch)));
        float lookZ = (float)(Math.sin(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch)));

        this.cameraFront = new Vector3f(-lookX,lookY,lookZ).normalize();

    }


    public void adjustProjection(){
        projectionMatrix.identity();
        projectionMatrix.perspective(45,(float) Window.get().width/Window.get().height,.1f,100f);
    }


    @Override
    public Matrix4f getViewMatrix(){
        viewMatrix.identity();
        viewMatrix.lookAt(this.position,
                new Vector3f(position.x + cameraFront.x,position.y + cameraFront.y,position.z + cameraFront.z),
                cameraUp);
        return viewMatrix;

    }

    @Override
    public Matrix4f getViewMatrixNoTranslation(){
        viewMatrixNoTranslation.identity();
        viewMatrixNoTranslation.lookAlong(this.cameraFront,this.cameraUp);
        return viewMatrixNoTranslation;
    }

    @Override
    public Matrix4f getProjectionMatrix(){
        return projectionMatrix;
    }

    public Vector3f getFrontVector(){
        return this.cameraFront.normalize();
    }

    public Vector3f getRightVector(){
        Vector3f result = new Vector3f();
        this.cameraUp.cross(this.cameraFront,result).normalize();
        return result;
    }

    public Vector3f getCameraPosition() {
        return position;
    }
}
