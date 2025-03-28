//package OpenGL_Basic.renderer.passes;
//
//import OpenGL_Basic.renderer.Shaders;
//import OpenGL_Basic.renderer.buffers.ColorBuffer;
//import OpenGL_Basic.renderer.buffers.DepthBuffer;
//import OpenGL_Basic.renderer.buffers.OutputBuffer;
//import OpenGL_Basic.util.Quad;
//import org.lwjgl.BufferUtils;
//import OpenGL_Basic.renderer.Renderer;
//
//import java.nio.FloatBuffer;
//
//import static OpenGL_Basic.renderer.Renderer.R_CAMERA_DEPTH_BUFFER;
//import static OpenGL_Basic.renderer.Renderer.R_OUTPUT_BUFFER;
//import static org.lwjgl.opengl.GL11.GL_FLOAT;
//import static org.lwjgl.opengl.GL15.*;
//import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
//import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
//import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
//import static org.lwjgl.opengl.GL30.glBindVertexArray;
//import static org.lwjgl.opengl.GL30.glGenVertexArrays;
//
//public class EdgePass implements RenderPass {
//    private static int[] dependencies = new int[]{R_CAMERA_DEPTH_BUFFER};
//    private Renderer renderer;
//    private ColorBuffer outputBuffer;
//    private DepthBuffer depthMap;
//    private int screenQuad;
//
//    public EdgePass(Renderer renderer){
//        this.renderer = renderer;
//        this.outputBuffer = (ColorBuffer) renderer.outputs.get(R_OUTPUT_BUFFER);
//        screenQuad  = glGenVertexArrays();
//        glBindVertexArray(screenQuad);
//
//        FloatBuffer quadVertexBuffer = BufferUtils.createFloatBuffer(Quad.quadVertices.length);
//        quadVertexBuffer.put(Quad.quadVertices).flip();
//
//        //VBO
//        int quadVBO = glGenBuffers();
//
//        glBindBuffer(GL_ARRAY_BUFFER,quadVBO);
//
//        glBufferData(GL_ARRAY_BUFFER,quadVertexBuffer,GL_STATIC_DRAW);
//
//        // XYZ
//        glEnableVertexAttribArray(0);
//        glVertexAttribPointer(0,2,GL_FLOAT,false,4 * 4,0);
//
//        // RGB
//        glEnableVertexAttribArray(1);
//        glVertexAttribPointer(1,2,GL_FLOAT,false,4 * 4,2 * 4);
//    }
//    @Override
//    public void render() {
//        outputBuffer.bindToWrite();
//        depthMap.bindToRead();
//
//        Shaders.edgeTestProgram.use();
//
//        Shaders.edgeTestProgram.uploadVec2f("depthMapDimensions",depthMap.getMapDimensions());
//        Shaders.edgeTestProgram.uploadInt("depthMap",6);
//
//        glBindVertexArray(screenQuad);
//        glDrawArrays(GL_TRIANGLES,0,6);
//
//        outputBuffer.detach();
//        depthMap.detach();
//        glBindVertexArray(0);
//
//    }
//    @Override
//    public OutputBuffer getBuffer() {return this.outputBuffer;}
//
//    public int[] getDependencies() {
//        return dependencies;
//    }
//
//    @Override
//    public void sourceDependencies() {
//        this.depthMap = (DepthBuffer) renderer.outputs.get(R_CAMERA_DEPTH_BUFFER);
//    }
//}
