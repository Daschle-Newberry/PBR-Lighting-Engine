package PBREngine.engine.scene.elements.model;

import PBREngine.engine.scene.elements.CubeMap;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Model extends Mesh{
    private int VAO;
    private CubeMap environmentMap;
    private int vertexCount;

    private Matrix4f position = new Matrix4f().translation(0f,0f,0f);
    private Matrix4f scale = new Matrix4f().scale(1f,1f,1f);
    private Matrix4f rotation = new Matrix4f().rotationXYZ(0f,0f,0f);
    private Matrix4f modelMatrix = new Matrix4f().identity();
    private Material material;

    public Model(String objFilePath,String matFilePath){
        this.material = new Material(matFilePath);
        float[] vertexArray = loadMeshFile(objFilePath);
        vertexCount = vertexArray.length/11;
        VAO = createVAO(vertexArray);

}

    public void render(){
            glBindVertexArray(VAO);
            glDrawArrays(GL_TRIANGLES,0,vertexCount);
            glBindVertexArray(0);
    }

    public void bindMaterial(){
        material.bind();
    }
    public void bindEnvironmentMap(){
        environmentMap.bindSpecularMap();
        environmentMap.bindDiffuseMap();
    }
    public int getMaterialType(){
        return material.getType();
    }

    public Matrix4f getModelMatrix(){
            modelMatrix.identity();
            scale.mul(modelMatrix,modelMatrix);
            rotation.mul(modelMatrix,modelMatrix);
            position.mul(modelMatrix,modelMatrix);
            return modelMatrix;
    }

    public void setScale(float scale){
            this.scale.identity();
            this.scale.scale(scale);
    }

    public void setPosition(Vector3f position){
            this.position.identity();
            this.position.translation(position);
    }

    public void setRotation(float angle, Vector3f axis){
            this.rotation.identity();
            this.rotation.rotation(angle,axis);
    }

    public void setEnvironmentMap(CubeMap environmentMap){
        this.environmentMap = environmentMap;
    }

}

