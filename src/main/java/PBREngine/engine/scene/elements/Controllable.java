package PBREngine.engine.scene.elements;

public interface Controllable {
    void processMouseMovement(float dX, float dY);
    void processMouseInput();
    void processKeyInput();
}
