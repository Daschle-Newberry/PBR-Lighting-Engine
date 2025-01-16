package OpenGL_Basic.engine;
import OpenGL_Basic.engine.Window;
import org.joml.Matrix4f;

import org.joml.Vector3f;

public class Camera {
    private int type;
    private Matrix4f projectionMatrix,viewMatrix,globalScale;

    private float pitch,yaw = 0.0f;

    private float sensitivity = .1f;

    private Vector3f position;

    private Vector3f heightOffset = new Vector3f(0.0f,2f,0.0f);

    private static Vector3f orbitPosition = new Vector3f(0.0f,0.0f,-1.0f);;

    private float lookX;
    private float lookY;
    private float lookZ;

    private Vector3f cameraFront = new Vector3f(0.0f,0f,-1f);
    private Vector3f cameraUp = new Vector3f(0.0f,1.0f,0.0f);


    public Camera(Vector3f position, int type){
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.globalScale = new Matrix4f().scale(1.0f,1.0f,1.0f);
        this.type = type;
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

        if (type == 0) {
            if (this.pitch >= 1f) {
                this.pitch = 1f;

            }
            if (this.pitch <= -1f) {
                this.pitch = -1f;
            }
        }
        else if (type == 1){
            if (this.pitch >= 89f) {
                this.pitch = 89f;

            }
            if (this.pitch <= -89f) {
                this.pitch = -89f;
            }

            this.lookX = (float)(Math.cos(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch)));
            this.lookY = (float)(Math.sin(Math.toRadians(this.pitch)));
            this.lookZ = (float)(Math.sin(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch)));

            this.cameraFront = new Vector3f(-this.lookX,this.lookY,this.lookZ).normalize();


        }
    }

    private void updateCameraVectors(){



    }
    public void adjustProjection(){
        projectionMatrix.identity();
        projectionMatrix.perspective(45,(float) Window.get().width/Window.get().height,0.1f,1000.0f);
    }

    public void adjustScale(float factor){
        Matrix4f factorMatrix = new Matrix4f().scale(factor,factor,factor);

        this.globalScale.mul(factorMatrix);

    }

    public void setFrontVector(Vector3f front){
        this.cameraFront = front;
    }

    public void setPosition(Vector3f position){this.position = position;}

    public void setCameraup(Vector3f up){
        this.cameraUp = up;
    }

    public void setProjectionMatrix(String type){
        if(type == "ortho"){
            this.projectionMatrix = new Matrix4f().ortho(-500,500,-500,500,0.1f,1000f);
        }
    }
    public Matrix4f getViewMatrix(){
        if (type == 0) {
            Matrix4f translation = new Matrix4f().translation(orbitPosition);
            Matrix4f rotation = new Matrix4f().rotationXYZ(-this.pitch, this.yaw, 0);
            this.viewMatrix = translation.mul(rotation);
        }
        else if (type == 1) {
            this.viewMatrix.identity();
            this.viewMatrix = viewMatrix.lookAt(this.position,
                    new Vector3f(this.position.x + this.cameraFront.x,this.position.y + this.cameraFront.y,this.position.z + this.cameraFront.z),
                    this.cameraUp);
        }
        return this.viewMatrix;

    }

    public Matrix4f getProjectionMatrix(){
        return this.projectionMatrix;
    }

    public Matrix4f getGlobalScale(){
        return this.globalScale;
    }

    public Vector3f getFrontVector(){
        return this.cameraFront;
    }

    public Vector3f getRightVector(){
        Vector3f result = new Vector3f();
        this.cameraUp.cross(this.cameraFront,result);
        return result;
    }

}
