package PBREngine.engine.scene.elements.model;

import PBREngine.util.FileReader;
import PBREngine.util.ImageLoader;
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
    public Material(String materialFilePath) {

        String format = FileReader.readLine(0,materialFilePath + "/tags");
        String imageType = FileReader.readLine(1,materialFilePath + "/tags");

        if(format.equals("ORM")){
            this.type = FORMAT_ORM;
            this.textures = new int[4];
            this.textures[0] = generateTexture(materialFilePath + "/albedo." + imageType);
            this.textures[1] = generateTexture(materialFilePath + "/normal." + imageType);
            this.textures[2] = generateTexture(materialFilePath + "/ao." + imageType);
            this.textures[3] = generateTexture(materialFilePath + "/ORM." + imageType);
        }else{
            this.type = FORMAT_REGULAR;
            this.textures = new int[5];
            this.textures[0] = generateTexture(materialFilePath + "/albedo." + imageType);
            this.textures[1] = generateTexture(materialFilePath + "/normal." + imageType);
            this.textures[2] = generateTexture(materialFilePath + "/ao." + imageType);
            this.textures[3] = generateTexture(materialFilePath + "/metallic." + imageType);
            this.textures[4] =  generateTexture(materialFilePath + "/roughness." + imageType);
        }
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

