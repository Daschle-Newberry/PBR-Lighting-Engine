package OpenGL_Basic.engine;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE7;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class ReflectionProbeGrid {
    private int brdfLUT, VAO, probeCount;
    private FloatBuffer instanceBuffer;
    private ReflectionProbe[] probes;
    public Matrix4f scale = new Matrix4f().scale(.1f);
    public ReflectionProbeGrid(float padding,int size, SceneData sceneData) {

        probeCount = size * size * size;

        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);
        int VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,VBO);

        instanceBuffer = BufferUtils.createFloatBuffer((probeCount) * 3);

        glEnableVertexAttribArray(0);


        glVertexAttribPointer(0,3,GL_FLOAT,false,3 * Float.BYTES,0);
        glVertexAttribDivisor(0,1);


        probes = new ReflectionProbe[probeCount];

        float[] tmpBuffer = new float[3];

        int index = 0;
        for(int x = -size / 2; x <= size/2; x++){
            for(int y = -size / 2; y <= size/2; y++){
                for(int z = -size / 2; z <= size/2; z++){
                    Vector3f position = new Vector3f(x,-.2f, z);
                    probes[index] = new ReflectionProbe(position, sceneData);
                    tmpBuffer[0] = position.x;
                    tmpBuffer[1] = position.y;
                    tmpBuffer[2] = position.z;
                    instanceBuffer.put(tmpBuffer);
                    index++;
                }
            }
        }

        update();
        glBufferData(GL_ARRAY_BUFFER,instanceBuffer.flip(),GL_STATIC_DRAW);





        glBindVertexArray(0);
    }

    public void debugRender(){
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP,probes[0].environmentID);
        glDrawArrays(GL_TRIANGLES,0,36);
    }

    public void debugBind(){
        glActiveTexture(GL_TEXTURE5);
        glBindTexture(GL_TEXTURE_CUBE_MAP,probes[0].diffuseID);

        glActiveTexture(GL_TEXTURE6);
        glBindTexture(GL_TEXTURE_CUBE_MAP,probes[0].specularID);

    }
    public ReflectionProbe findNearestProbe(Vector3f position){
        float minDistance = 0;
        ReflectionProbe closestProbe = null;
        boolean isFirst = true;

        for(ReflectionProbe probe : probes){
            Vector3f displacement = new Vector3f();
            position.sub(probe.getPosition(),displacement);
            float distance = (displacement.x * displacement.x) + (displacement.y * displacement.y) + (displacement.z * displacement.z);
            if(isFirst){
                minDistance = distance;
                closestProbe = probe;
                isFirst = false;
            }else if(distance < minDistance){
                minDistance = distance;
                closestProbe = probe;
            }
        }
        
        return  closestProbe;
    }
    public void update(){
        for(ReflectionProbe probe : probes){
            probe.update();
        }
    }

    public void render(){
        glBindVertexArray(VAO);
        glDrawArraysInstanced(GL_TRIANGLES,0,36,probeCount);
        glBindVertexArray(0);
    }
    public void bindBRDFLUT(){
        glActiveTexture(GL_TEXTURE7);
        glBindTexture(GL_TEXTURE_2D, brdfLUT);
    }
}
