package OpenGL_Basic.engine.scene;

public abstract class Scene {
    public Scene(){

    }

    public abstract void init();

    public abstract void update(double dt);

    public abstract void render();

}

