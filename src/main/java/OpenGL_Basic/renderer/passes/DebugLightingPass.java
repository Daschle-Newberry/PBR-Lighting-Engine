package OpenGL_Basic.renderer.passes;

import OpenGL_Basic.engine.Camera;
import OpenGL_Basic.engine.Model;
import OpenGL_Basic.renderer.Renderer;
import OpenGL_Basic.renderer.Shader;
import OpenGL_Basic.renderer.Shaders;
import OpenGL_Basic.renderer.UniformBlock;
import OpenGL_Basic.renderer.buffers.Buffer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;

import static OpenGL_Basic.renderer.Renderer.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class DebugLightingPass extends RenderPass {
    private static Shader shader = Shaders.debugProgram;
    private UniformBlock uniforms;
    private Renderer renderer;

    public DebugLightingPass(Renderer renderer) {
        this.renderer = renderer;

        if(!shader.isCompiled) shader.compile();


        this.uniforms = shader.getUniformBlock();

    }




    @Override
    public void render() {
        glBindFramebuffer(GL_FRAMEBUFFER,0);
        shader.use();
        glClear(GL_DEPTH_BUFFER_BIT);
        glClear(GL_COLOR_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        for(String mat4 : uniforms.mat4f){
            shader.uploadMat4f(mat4,(Matrix4f) renderer.getData(mat4));
        }
        for(String vec3 : uniforms.vec3f){
            shader.uploadVec3f(vec3,(Vector3f) renderer.getData(vec3));
        }

        for(Model model : renderer.models){
            shader.uploadMat4f("modelMatrix",model.getModelMatrix());
            model.bindMaterial();
            shader.uploadInt("color",0);
            model.render();
        }



        Shaders.debugProgram.detach();
        glBindVertexArray(0);
    }

    @Override
    public Buffer getBuffer() {
        return null;
    }
}
