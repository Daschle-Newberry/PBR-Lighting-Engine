package OpenGL_Basic.renderer.buffers;

public abstract class OutputBuffer {

    public abstract void bindToWrite();
    public abstract void bindToRead();
    public abstract void detach();


}
