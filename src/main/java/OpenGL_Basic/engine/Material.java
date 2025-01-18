package OpenGL_Basic.engine;

import OpenGL_Basic.util.ImageLoader;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;


public class Material {
    private static int textureCount;
    private int albedo,normal,metallic,roughness,AO;
    public Material(String albedo,String normal, String metallic, String roughness, String AO) {

        this.albedo = generateTexture(albedo);
        this.normal = generateTexture(normal);
        this.metallic = generateTexture(metallic);
        this.roughness = generateTexture(roughness);
        this.AO = generateTexture(AO);

    }

    private int generateTexture(String filePath){
        if (filePath == null){
            return 0;
        }
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        ByteBuffer textureIMG  = ImageLoader.loadImage(filePath,width,height,channels);

        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, textureIMG);

        glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        return textureID;
    }
    public void bind(){
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,albedo);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D,normal);

        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D,metallic);

        glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D,roughness);

        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D,AO);

    }
}

