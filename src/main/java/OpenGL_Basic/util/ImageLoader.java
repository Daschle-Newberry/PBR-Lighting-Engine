package OpenGL_Basic.util;

import OpenGL_Basic.engine.Material;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;

public class ImageLoader {

    public static ByteBuffer loadImage(String filePath,IntBuffer width, IntBuffer height, IntBuffer channels){
        try(InputStream inputStream = Material.class.getResourceAsStream(filePath)){
            assert inputStream != null : "Image not found '" + filePath + "'";

            byte[] imageData = inputStream.readAllBytes();

            ByteBuffer imageBuffer = BufferUtils.createByteBuffer(imageData.length);
            imageBuffer.put(imageData);
            imageBuffer.flip();

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
