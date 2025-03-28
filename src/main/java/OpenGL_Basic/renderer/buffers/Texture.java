package OpenGL_Basic.renderer.buffers;

import OpenGL_Basic.engine.Window;
import org.joml.Vector2f;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public class Texture implements Sampler2D {
    private int textureID,bindingPoint;
    private float width,height;

    public Texture(int internal, int format, float width, float height,int bindingPoint){
        this.height = height;
        this.width = width;
        this.bindingPoint = bindingPoint;

        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D,0,internal, Window.get().width,Window.get().height,
              0,format,GL_FLOAT,(FloatBuffer) null
                    );
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_BORDER);

    }

    @Override
    public void bindTo(int binding){
        glActiveTexture(GL_TEXTURE0 + binding);
        glBindTexture(GL_TEXTURE_2D,textureID);
    }

    @Override
    public void bind(){
        glActiveTexture(GL_TEXTURE0 + bindingPoint);
        glBindTexture(GL_TEXTURE_2D,textureID);
    }

    @Override
    public int getTextureID(){return this.textureID;}

    @Override
    public Vector2f getDimensions(){return new Vector2f(width,height);}
}
