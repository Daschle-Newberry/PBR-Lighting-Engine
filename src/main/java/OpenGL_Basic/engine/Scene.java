package OpenGL_Basic.engine;

public abstract class Scene {
    public Scene(){

    }

    public abstract void init();

    public abstract void framebufferSizeCallback(long window, int width, int height);

    public abstract void update(double dt);

    public abstract void render();

}

