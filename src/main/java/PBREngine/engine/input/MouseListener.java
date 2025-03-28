package PBREngine.engine.input;

import static org.lwjgl.glfw.GLFW.*;


public class MouseListener {
    public static MouseListener instance;

    private boolean hasMoved;
    private double scrollX,scrollY;
    private double xPos,yPos,lastY,lastX,dX,dY;

    private boolean mouseButtonPressed[] = new boolean[3];
    private boolean isDragging;
    private MouseListener(){
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;

        this.dX = 0;
        this.dY = 0;
    }

    public static MouseListener get(){
        if (MouseListener.instance == null){
            MouseListener.instance = new MouseListener();
        }
        return MouseListener.instance;
    }

    public static void mousePosCallBack(long window,double xpos,double ypos){
        get().lastX = get().xPos;
        get().lastY = get().yPos;

        get().xPos = xpos;
        get().yPos = ypos;

        get().dX += (get().lastX - get().xPos);
        get().dY += (get().lastY - get().yPos);

        get().isDragging = get().mouseButtonPressed[0] |  get().mouseButtonPressed[1] |  get().mouseButtonPressed[2];


    }

    public static void mouseButtonCallBack(long window,int button, int action, int mods){
        if(action == GLFW_PRESS) {
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE){
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset){
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame(){
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getX(){
        return (float)get().xPos;
    }

    public static float getY(){
        return (float)get().yPos;
    }

    public static float getDx(){
        return (float)(get().dX);
    }

    public static float getDy(){
        return (float)(get().dY);
    }

    public static float getScrollX(){
        return (float)get().scrollX;
    }

    public static float getScrollY(){
        return (float)get().scrollY;
    }

    public static boolean isDragging(){
        return get().isDragging;
    }
    public static boolean hasMoved(){
        return get().hasMoved;
    }

    public static void proccessMovement(){
        get().dX = 0;
        get().dY = 0;
    }

    public static boolean mouseButtonDown(int button){
        if (button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        } else {
            return false;
        }
    }
}
