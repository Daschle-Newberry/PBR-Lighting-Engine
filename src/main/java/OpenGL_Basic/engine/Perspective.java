package OpenGL_Basic.engine;

import org.joml.Matrix4f;

public abstract class Perspective {
    public abstract Matrix4f getViewMatrix();
    public abstract Matrix4f getProjectionMatrix();

}
