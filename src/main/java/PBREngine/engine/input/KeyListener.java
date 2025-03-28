package PBREngine.engine.input;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
public class KeyListener {
    private static KeyListener instance;
    private boolean keyHeld[] = new boolean[350];

    private boolean keyToggled[] = new boolean[350];

    private KeyListener(){

    }
    public static KeyListener get(){
        if (KeyListener.instance == null){
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    }
    public static void keyCallBack(long window,int key, int scancode, int action, int mods){
        if (action == GLFW_PRESS){
            get().keyHeld[key] = true;
            get().keyToggled[key] = !get().keyToggled[key];

        } else if (action == GLFW_RELEASE){
            get().keyHeld[key] = false;
        }
    }
    public boolean isKeyPressed(int keyCode){
        if (keyCode < get().keyHeld.length) {
            return get().keyHeld[keyCode];
        } else {
            return false;
        }
    }

    public static boolean isKeyToggled(int keyCode){
        if (keyCode < get().keyToggled.length) {
            return get().keyToggled[keyCode];
        } else {
            return false;
        }
    }
}
