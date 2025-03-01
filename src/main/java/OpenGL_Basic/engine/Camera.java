package OpenGL_Basic.engine;
import OpenGL_Basic.engine.Window;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import org.joml.Vector3f;

public class Camera implements Perspective {
    private static float sensitivity = .1f;

    private Matrix4f projectionMatrix,viewMatrix,globalScale;
    private float pitch,yaw = 0.0f;
    private Vector3f position;
    private float nearPlane, farPlane;
    private Vector3f cameraFront = new Vector3f(0.0f,0f,-1f);
    private Vector3f cameraUp = new Vector3f(0.0f,1.0f,0.0f);


    public Camera(Vector3f position){
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.globalScale = new Matrix4f().scale(1.0f,1.0f,1.0f);
        this.nearPlane = .1f;
        this.farPlane = 100.0f;
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
        projectionMatrix.perspective(45,(float) Window.get().width/Window.get().height,nearPlane,farPlane);
    }


    @Override
    public Matrix4f getViewMatrix(){
        this.viewMatrix.identity();
        this.viewMatrix = viewMatrix.lookAt(this.position,
                new Vector3f(this.position.x + this.cameraFront.x,this.position.y + this.cameraFront.y,this.position.z + this.cameraFront.z),
                this.cameraUp);

        return this.viewMatrix;

    }

    @Override
    public Matrix4f getViewMatrixNoTranslation(){
        this.viewMatrix.identity();

        Matrix3f viewMinusTranslation = new Matrix3f().lookAlong(this.cameraFront,this.cameraUp);
        this.viewMatrix = viewMatrix.set(viewMinusTranslation);
        return this.viewMatrix;
    }


    @Override
    public Matrix4f getProjectionMatrix(){
        return this.projectionMatrix;
    }


    @Override
    public float getNearPlane() {return  this.nearPlane;}

    @Override
    public float getFarPlane() {return  this.farPlane;}

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
