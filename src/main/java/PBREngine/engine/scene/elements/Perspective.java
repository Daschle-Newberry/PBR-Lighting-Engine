package PBREngine.engine.scene.elements;

import org.joml.Matrix4f;

public interface Perspective {
    Matrix4f getViewMatrix();
    Matrix4f getProjectionMatrix();
    Matrix4f getViewMatrixNoTranslation();

}
