package OpenGL_Basic.engine;

import OpenGL_Basic.renderer.Shaders;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Model{
    private int VAO;
    private int vertexCount;
    private static int floatBytes = 4;

    private Matrix4f position = new Matrix4f().translation(0f,0f,0f);
    private Matrix4f scale = new Matrix4f().scale(1f,1f,1f);
    private Matrix4f rotation = new Matrix4f().rotationXYZ(0f,0f,0f);

    private Matrix4f modelMatrix = new Matrix4f().identity();
    private Material material;
    public Model(String filePath,Material material){
        this.material = material;
        float[] vertexArray = loadModelFile(filePath);
        vertexCount = (int)vertexArray.length/3;


        //VAO
        VAO  = glGenVertexArrays();
        glBindVertexArray(VAO);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //VBO
        int VBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER,VBO);

        glBufferData(GL_ARRAY_BUFFER,vertexBuffer,GL_STATIC_DRAW);

        // XYZ
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0,3,GL_FLOAT,false,11 * floatBytes,0);

        // UV
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1,2,GL_FLOAT,false,11 * floatBytes,3 * floatBytes);

        //Normals
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2,3,GL_FLOAT,false,11 * floatBytes,5 * floatBytes);


        //Tangents
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3,3,GL_FLOAT,false,11 * floatBytes,8 * floatBytes);
}

    public void render(){
            glBindVertexArray(VAO);
            glDrawArrays(GL_TRIANGLES,0,vertexCount);
            glBindVertexArray(0);
    }

    public void bindMaterial(){
        material.bind();
    }

    public Matrix4f getModelMatrix(){
            modelMatrix.identity();
            scale.mul(position,modelMatrix);
            rotation.mul(modelMatrix,modelMatrix);
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
    public static float[] loadModelFile(String filePath) {
        InputStream modelDataStream = Model.class.getResourceAsStream(filePath);

        if(modelDataStream == null){
            throw new RuntimeException("Model file not found");
        }
        //Read files
        File tempFile;
        try {
            tempFile = File.createTempFile("model_", ".obj");
            tempFile.deleteOnExit();
        }catch(IOException e){
            throw new RuntimeException("Error creating temp file",e);

        }


        try (OutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while((bytesRead = modelDataStream.read(buffer)) != -1){
                out.write(buffer,0,bytesRead);
            }
        }catch(IOException e){
            throw new RuntimeException("Error writing to temp file",e);
        }
        AIScene scene = Assimp.aiImportFile(tempFile.getAbsolutePath(),Assimp.aiProcess_Triangulate | Assimp.aiProcess_GenSmoothNormals|Assimp.aiProcess_CalcTangentSpace);
        PointerBuffer buffer = scene.mMeshes();
        ArrayList<Float> vertexAttributesList = new ArrayList<Float>();
        for(int i = 0; i < buffer.limit(); i++){
            AIMesh mesh =AIMesh.create(buffer.get(i));
            processMesh(mesh,vertexAttributesList);
        }
        float[] vertexAttributes = new float[vertexAttributesList.size()];
        for(int i = 0; i < vertexAttributesList.size(); i++){
            vertexAttributes[i] = vertexAttributesList.get(i);
        }
        int index = 0;
        for (int q = 0; q < vertexAttributes.length; q++){
            index += 1;
            if (index == 9){
                index = 0;
            }
        }
        return vertexAttributes;
    }

    private static void processMesh(AIMesh mesh, ArrayList<Float> vertexAttributesList){
        AIVector3D.Buffer positionVectors = mesh.mVertices();
        AIVector3D.Buffer textureCoordinates = mesh.mTextureCoords(0);
        AIVector3D.Buffer normalVectors = mesh.mNormals();
        AIVector3D.Buffer tangentVectors = mesh.mTangents();

        AIFace.Buffer faces = mesh.mFaces();

        for(int i = 0; i < mesh.mFaces().limit(); i++){
            AIFace face = faces.get(i);

            IntBuffer indices = face.mIndices();

            for(int j = 0; j < indices.limit(); j++){
                int vertexIndex = indices.get(j);
                AIVector3D position = positionVectors.get(vertexIndex);

                vertexAttributesList.add(position.x());
                vertexAttributesList.add(position.y());
                vertexAttributesList.add(position.z());

                if(textureCoordinates != null){
                    AIVector3D texCoords = textureCoordinates.get(vertexIndex);

                    vertexAttributesList.add(texCoords.x());
                    vertexAttributesList.add(texCoords.y());
                } else{
                    vertexAttributesList.add(0f);
                    vertexAttributesList.add(0f);
                }



                AIVector3D normals = normalVectors.get(vertexIndex);

                vertexAttributesList.add(normals.x());
                vertexAttributesList.add(normals.y());
                vertexAttributesList.add(normals.z());

                AIVector3D tangents = tangentVectors.get(vertexIndex);
                vertexAttributesList.add(tangents.x());
                vertexAttributesList.add(tangents.y());
                vertexAttributesList.add(tangents.z());



            }
        }
    }
}

