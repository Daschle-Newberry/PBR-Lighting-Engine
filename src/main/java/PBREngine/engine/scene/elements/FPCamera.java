package PBREngine.engine.scene.elements;
import PBREngine.engine.Window;
import PBREngine.engine.input.KeyListener;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

public class FPCamera implements Camera,Controllable {
    private static float sensitivity = .1f;
    private static float speed = .005f;
    private Matrix4f projectionMatrix,viewMatrix,viewMatrixNoTranslation;
    private float pitch,yaw = 0.0f;
    private Vector3f position,cameraUp,cameraFront,cameraRight;

    public FPCamera(Vector3f position){
        this.position = position;
        viewMatrix = new Matrix4f();
        viewMatrixNoTranslation = new Matrix4f();
        projectionMatrix = new Matrix4f();
        projectionMatrix.perspective(45,(float) Window.get().width/Window.get().height,.1f,100f);
        cameraFront = new Vector3f(0.0f,0f,-1f);
        cameraUp = new Vector3f(0.0f,1.0f,0.0f);
        cameraRight = new Vector3f();
        cameraUp.cross(this.cameraFront,cameraRight).normalize();

    }

    @Override
    public void processMouseMovement(float dX, float dY){
        float xOffset = dX * sensitivity;
        float yOffset = dY  * sensitivity;

        yaw += xOffset;
        pitch += yOffset;

        if (pitch >= 89f) {
            pitch = 89f;

        }
        if (pitch <= -89f) {
            pitch = -89f;
        }

        float lookX = (float)(Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        float lookY = (float)(Math.sin(Math.toRadians(pitch)));
        float lookZ = (float)(Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));

        cameraFront = new Vector3f(-lookX,lookY,lookZ).normalize();
        cameraUp.cross(cameraFront,cameraRight).normalize();

    }

    @Override
    public void processMouseInput() {
    }

    @Override
    public void processKeyInput() {

        if (KeyListener.get().isKeyPressed(GLFW_KEY_W)) {
            position.x += cameraFront.x * speed;
            position.y += cameraFront.y * speed;
            position.z += cameraFront.z * speed;
        }
        if (KeyListener.get().isKeyPressed(GLFW_KEY_S)) {
            position.x -= cameraFront.x * speed;
            position.y -= cameraFront.y * speed;
            position.z -= cameraFront.z * speed;
        }
        if (KeyListener.get().isKeyPressed(GLFW_KEY_D)) {
            position.x -= cameraRight.x * speed;
            position.y -= cameraRight.y * speed;
            position.z -= cameraRight.z * speed;
        }
        if (KeyListener.get().isKeyPressed(GLFW_KEY_A)) {
            position.x += cameraRight.x * speed;
            position.y += cameraRight.y * speed;
            position.z += cameraRight.z * speed;
        }
    }

    @Override
    public Matrix4f getViewMatrix(){
        viewMatrix.identity();
        viewMatrix.lookAt(position,
                new Vector3f(position.x + cameraFront.x,position.y + cameraFront.y,position.z + cameraFront.z),
                cameraUp);
        return viewMatrix;

    }
    @Override
    public Matrix4f getViewMatrixNoTranslation(){
        viewMatrixNoTranslation.identity();
        viewMatrixNoTranslation.lookAlong(this.cameraFront,this.cameraUp);
        return viewMatrixNoTranslation;
    }
    @Override
    public Matrix4f getProjectionMatrix(){
        return projectionMatrix;
    }
    @Override
    public Vector3f getFrontVector(){
        return this.cameraFront.normalize();
    }
    @Override
    public Vector3f getRightVector(){
        Vector3f result = new Vector3f();
        this.cameraUp.cross(this.cameraFront,result).normalize();
        return result;
    }
    @Override
    public Vector3f getPosition() {
        return position;
    }
    @Override
    public void setPosition(Vector3f position) {
    }
}
