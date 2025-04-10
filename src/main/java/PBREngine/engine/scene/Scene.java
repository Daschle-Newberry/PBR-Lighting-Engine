package PBREngine.engine.scene;

public abstract class Scene {
    public Scene(){

    }

    public abstract void init();

    public abstract void resize();
    public abstract void update(double dt);

    public abstract void render();


}

