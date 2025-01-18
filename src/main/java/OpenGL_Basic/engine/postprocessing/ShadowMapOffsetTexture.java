package OpenGL_Basic.engine.postprocessing;

import org.lwjgl.BufferUtils;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_3D;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.glTexParameterIi;
import static org.lwjgl.opengl.GL42C.glTexStorage3D;


public class ShadowMapOffsetTexture {

    public static int createShadowOffsetTexture(int windowSize, int filterSize){
        FloatBuffer BufferData = genTextureData(windowSize,filterSize);

        int numFIlterSamples = filterSize * filterSize;

        glActiveTexture(GL_TEXTURE2);
        int bufferTexture = glGenTextures();

        glBindTexture(GL_TEXTURE_3D,bufferTexture);
        glTexStorage3D(GL_TEXTURE_3D,1,GL_RGBA32F, numFIlterSamples/2,windowSize,windowSize);
        glTexSubImage3D(GL_TEXTURE_3D,0,0,0,0,numFIlterSamples/2,windowSize,windowSize,GL_RGBA,GL_FLOAT,BufferData);

        glTexParameteri(GL_TEXTURE_3D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
        glTexParameteri(GL_TEXTURE_3D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);

        glBindTexture(GL_TEXTURE_3D,0);

        return bufferTexture;
    }
    private static float randomOffset(){
        return (float) (Math.random() - .5);
    }
    private static FloatBuffer genTextureData(int windowSize, int filterSize){
        int bufferSize = windowSize * windowSize * filterSize * filterSize * 2;
        float[] bufferData =  new float[bufferSize];

        int index = 0;

        for(int TexY = 0; TexY < windowSize; TexY ++){
            for(int TexX = 0; TexX < windowSize; TexX ++){
                for(int v = filterSize - 1; v >= 0; v--){
                    for(int u = filterSize - 1; u >= 0; u--){
                        float x = (u + 0.5f + randomOffset())/ (float) filterSize;
                        float y = (v + 0.5f + randomOffset())/ (float) filterSize;

                        bufferData[index] = (float) (Math.sqrt(y) * Math.cos(2 * Math.PI * x));
                        bufferData[index + 1] = (float) (Math.sqrt(y) * Math.sin(2 * Math.PI * x));

                        index += 2;
                    }
                }
            }
        }

        FloatBuffer buffer = BufferUtils.createFloatBuffer(bufferSize);
        buffer.put(bufferData);

        return buffer;
    }
}
