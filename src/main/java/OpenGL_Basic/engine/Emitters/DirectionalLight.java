package OpenGL_Basic.engine.Emitters;

import OpenGL_Basic.engine.Perspective;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class DirectionalLight extends Perspective {
    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f projectionMatrix = new Matrix4f();

    private Vector3f position;
    private Vector3f lightFront;
    private Vector3f lightUp = new Vector3f(0.0f,1.0f,0.0f);

    public DirectionalLight(Vector3f position, Vector3f direction){
        this.position = position;
        this.lightFront = direction;
        this.projectionMatrix = new Matrix4f().identity();
        projectionMatrix.ortho(-1,1,-1,1,0.1f,20f);
    }
    public Matrix4f getViewMatrix(){
        this.viewMatrix.identity();
        this.viewMatrix = viewMatrix.lookAt(this.position,
                new Vector3f(this.position.x - this.lightFront.x,this.position.y - this.lightFront.y,this.position.z - this.lightFront.z),
                this.lightUp);
        return this.viewMatrix;

    }
    public Matrix4f getProjectionMatrix(){return this.projectionMatrix;}

    public void setLightPos(Vector3f newPos){this.position = newPos;}

    public void setLightFront(Vector3f newFront){
        this.lightFront = newFront;
        this.lightFront.normalize();}
    public Vector3f getPosition(){
        return this.position;
    }
    public Vector3f getLightFront(){return this.lightFront;}

}


