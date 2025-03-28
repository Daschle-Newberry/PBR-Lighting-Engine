package OpenGL_Basic.util;

import OpenGL_Basic.engine.scene.elements.model.Material;
import org.lwjgl.BufferUtils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;

public class ImageLoader {
    public static FloatBuffer loadHDRImage(String filePath,IntBuffer width, IntBuffer height, IntBuffer channels){
        try(InputStream inputStream = Material.class.getResourceAsStream(filePath)){
            assert inputStream != null : "Image not found '" + filePath + "'";

            byte[] imageData = inputStream.readAllBytes();

            ByteBuffer imageBuffer = BufferUtils.createByteBuffer(imageData.length);
            imageBuffer.put(imageData);
            imageBuffer.flip();

            stbi_set_flip_vertically_on_load(true);
            FloatBuffer textureIMG = stbi_loadf_from_memory(imageBuffer,width,height,channels,0);

            if (textureIMG == null) throw new RuntimeException("Failed to load image " + filePath);

            return textureIMG;

        }catch(Exception e){
            throw new RuntimeException("Failed to load image '" + filePath + "'");
        }
    }
    public static ByteBuffer loadImage(String filePath,IntBuffer width, IntBuffer height, IntBuffer channels){
        try(InputStream inputStream = Material.class.getResourceAsStream(filePath)){
            assert inputStream != null : "Image not found '" + filePath + "'";

            byte[] imageData = inputStream.readAllBytes();

            ByteBuffer imageBuffer = BufferUtils.createByteBuffer(imageData.length);
            imageBuffer.put(imageData);
            imageBuffer.flip();

            stbi_set_flip_vertically_on_load(true);
            ByteBuffer textureIMG = stbi_load_from_memory(imageBuffer, width, height, channels, STBI_rgb_alpha);

            if (textureIMG == null) {
                assert false : "Failed to load image " + filePath;
            }

            return textureIMG;

        }catch(Exception e){
            throw new RuntimeException("Failed to load image '" + filePath + "'");
        }
    }
}
