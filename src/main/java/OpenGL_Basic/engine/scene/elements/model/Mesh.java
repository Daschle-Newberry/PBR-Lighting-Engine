package OpenGL_Basic.engine.scene.elements.model;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public abstract class Mesh {
    static float[] loadMeshFile(String filePath) {
        File tmpFile;
        try (InputStream inputStream = Material.class.getResourceAsStream(filePath)) {
            if (inputStream == null) throw new IOException("Cannot find mesh file '" + filePath + "'");
            byte[] fileBuffer = inputStream.readAllBytes();
            tmpFile = File.createTempFile("model_",".obj");
            Files.write(tmpFile.toPath(),fileBuffer);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AIScene scene = Assimp.aiImportFile(tmpFile.getAbsolutePath(), Assimp.aiProcess_CalcTangentSpace| Assimp.aiProcess_Triangulate);
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

    static int createVAO(float[] vertexArray){
        //VAO
        int VAO  = glGenVertexArrays();
        glBindVertexArray(VAO);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //VBO
        int VBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER,VBO);

        glBufferData(GL_ARRAY_BUFFER,vertexBuffer,GL_STATIC_DRAW);

        // XYZ
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0,3,GL_FLOAT,false,11 * Float.BYTES,0);

        // UV
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1,2,GL_FLOAT,false,11 * Float.BYTES,3 * Float.BYTES);

        //Normals
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2,3,GL_FLOAT,false,11 * Float.BYTES,5 * Float.BYTES);


        //Tangents
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3,3,GL_FLOAT,false,11 * Float.BYTES,8 * Float.BYTES);

        return VAO;
    }
    static void processMesh(AIMesh mesh, ArrayList<Float> vertexAttributesList){
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

                if(tangentVectors != null){
                    AIVector3D tangents = tangentVectors.get(vertexIndex);
                    vertexAttributesList.add(tangents.x());
                    vertexAttributesList.add(tangents.y());
                    vertexAttributesList.add(tangents.z());
                }else{
                    vertexAttributesList.add(0f);
                    vertexAttributesList.add(0f);
                    vertexAttributesList.add(0f);
                }



            }
        }
    }
}
