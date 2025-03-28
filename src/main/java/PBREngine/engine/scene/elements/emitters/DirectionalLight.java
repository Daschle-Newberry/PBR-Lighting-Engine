package PBREngine.engine.scene.elements.emitters;

import PBREngine.engine.scene.elements.Perspective;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class DirectionalLight implements Perspective {
    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f projectionMatrix = new Matrix4f();

    private Vector3f position;
    private Vector3f center;
    private Vector3f lightFront;
    private Vector3f lightUp = new Vector3f(0.0f,1.0f,0.0f);

    public DirectionalLight(Vector3f position, Vector3f center, Matrix4f projectionMatrix){
        this.position = position;
        this.center = center;
        this.lightFront = new Vector3f();
        center.sub(position,this.lightFront);
        lightFront.normalize();
        this.projectionMatrix = projectionMatrix;
    }


    @Override
    public Matrix4f getViewMatrix(){
        viewMatrix.identity();
        viewMatrix = viewMatrix.lookAt(position,
                center,
                lightUp);
        return viewMatrix;

    }

    @Override
    public Matrix4f getProjectionMatrix(){return this.projectionMatrix;}

    @Override
    public Matrix4f getViewMatrixNoTranslation() {
        return null;
    }

    public void setLightPos(Vector3f newPos){this.position = newPos;}


    public Vector3f getPosition(){
        return this.position;
    }
    public Vector3f getLightFront(){return this.lightFront;}
    public float getIntensity(){return 3.0f;}
    public Vector3f getColor(){return new Vector3f(1.0f);}
}


