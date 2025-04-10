package PBREngine.engine.scene.elements;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface Camera {
    Matrix4f getViewMatrix();
    Matrix4f getProjectionMatrix();
    Matrix4f getViewMatrixNoTranslation();
    Vector3f getFrontVector();
    Vector3f getRightVector();
    Vector3f getPosition();
    void setPosition(Vector3f position);
}
