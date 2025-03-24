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
    public static final int FORMAT_REGULAR = 0;
    public static final int FORMAT_ORM = 1;

    private int type;
    private int[] textures;
    public Material(String albedo,String normal, String metallic, String roughness, String AO) {
        this.type = FORMAT_REGULAR;

        this.textures = new int[5];
        this.textures[0] = generateTexture(albedo);
        this.textures[1] = generateTexture(normal);
        this.textures[2] = generateTexture(AO);
        this.textures[3] = generateTexture(metallic);
        this.textures[4] = generateTexture(roughness);

    }

    public Material(String albedo,String normal, String ORM,String AO) {
        this.type = FORMAT_ORM;

        this.textures = new int[5];
        this.textures[0] = generateTexture(albedo);
        this.textures[1] = generateTexture(normal);
        this.textures[2] = generateTexture(AO);
        this.textures[3] = generateTexture(ORM);

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
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, textureIMG);

        glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        return textureID;
    }

    public int getType(){return this.type;}
    public void bind(){
        for(int i = 0; i < textures.length; i++){
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(GL_TEXTURE_2D,textures[i]);
        }

    }
}

