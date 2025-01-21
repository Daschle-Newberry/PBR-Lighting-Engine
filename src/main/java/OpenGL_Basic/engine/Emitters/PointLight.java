package OpenGL_Basic.engine.Emitters;

import org.joml.Vector3f;

public class PointLight {
    private Vector3f position;
    private float intensity;
    private Vector3f color;

    public PointLight(Vector3f position,float intensity,Vector3f color){
        this.position = position;
        this.intensity = intensity;
        this.color = color;
    }

    public Vector3f getPosition(){return this.position;}
    public Vector3f getColor(){return this.color;}
    public float getIntensity(){return this.intensity;}

}
