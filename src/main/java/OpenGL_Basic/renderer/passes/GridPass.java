package OpenGL_Basic.renderer.passes;

import OpenGL_Basic.engine.Window;
import OpenGL_Basic.engine.gameobjects.Camera;
import OpenGL_Basic.renderer.Renderer;
import OpenGL_Basic.renderer.Shader;
import OpenGL_Basic.renderer.Shaders;
import OpenGL_Basic.renderer.buffers.FrameBuffer;

import static org.lwjgl.opengl.GL11.*;

public class GridPass extends RenderPass {

    private static Shader shader = Shaders.gridProgram;
    private Renderer renderer;
    private FrameBuffer FBO;

    public GridPass(Renderer renderer, int[] colorBufferRequest,int depthBufferRequest){
        if(!shader.isCompiled) shader.compile();
        this.renderer = renderer;
        FBO = createFrameBuffer(colorBufferRequest,depthBufferRequest,renderer);
    }
    @Override
    public void render(){
        FBO.bindToWrite();
        shader.use();
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        Camera sceneCamera = renderer.sceneCamera;

        Shaders.gridProgram.use();

        Shaders.gridProgram.uploadMat4f("viewMatrix",sceneCamera.getViewMatrix());
        Shaders.gridProgram.uploadMat4f("projectionMatrix", sceneCamera.getProjectionMatrix());
        Shaders.gridProgram.uploadVec3f("cameraPosition", sceneCamera.getCameraPosition());


        glDrawArrays(GL_TRIANGLES,0,6);


        FBO.detach();
        Shaders.gridProgram.detach();
    }

}
