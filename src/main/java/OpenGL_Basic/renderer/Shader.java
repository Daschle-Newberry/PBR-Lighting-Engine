package OpenGL_Basic.renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL30.*;

public class Shader {
    private int vertexID,fragmentID,shaderProgram;
    private String vertexShaderSrc,fragmentShaderSrc;
    private String vertex_path, fragment_path;

    private FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
    private FloatBuffer vec3fBuffer = BufferUtils.createFloatBuffer(3);
    private FloatBuffer vec2fBuffer = BufferUtils.createFloatBuffer(2);

    public Shader(String vert_path,String frag_path){
        vertex_path = vert_path;
        fragment_path = frag_path;


        InputStream vertexStream = Shaders.class.getResourceAsStream(vert_path);

        if(vertexStream == null){
            throw new RuntimeException("Shader file not found");
        }
        //Read files
        try {
            vertexShaderSrc = new String(vertexStream.readAllBytes(), StandardCharsets.UTF_8);
        }catch(IOException e){
            e.printStackTrace();
            assert false : "Error: Could not open file : '" + vert_path + "'";
        }


        InputStream fragmentStream = Shaders.class.getResourceAsStream(fragment_path);

        try {
            fragmentShaderSrc = new String(fragmentStream.readAllBytes(), StandardCharsets.UTF_8);
        }catch(IOException e){
            e.printStackTrace();
            assert false : "Error: Could not open file : '" + frag_path + "'";
        }

        //Compile and link shaders
        //Vertex Shader
        System.out.println("Compiling Vertex Shader");
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexShaderSrc);

        glCompileShader(vertexID);

        //Check for errors

        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);

        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: " + vertex_path + "\n\tVertex shader compiliation failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));

            assert false : " ";
        }
        System.out.println("Success");

        //Fragment Shader
        System.out.println("Compiling Fragment Shader");
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentShaderSrc);

        glCompileShader(fragmentID);

        //Check for errors

        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);

        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: " + fragment_path + "\n\tFragment shader compiliation failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));

            assert false : " ";
        }
        System.out.println("Success");
    }

    public void compile(){
        System.out.println("Compiling Program");
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        //Check for linking errors

        int success = glGetProgrami(shaderProgram, GL_LINK_STATUS);

        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);

            System.out.println("Error: Program:\n\t" + vertex_path + "\n\t" + fragment_path +  "\n Shader compilation failed");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));

            assert false : " ";
        }
        System.out.println("Success");
    }

    public void use(){
        glUseProgram(shaderProgram);
    }
    public void detach(){
        glUseProgram(0);
    }

    public void uploadMat4f(String varName, Matrix4f mat4){
        int location = glGetUniformLocation(this.shaderProgram,varName);
        glUniformMatrix4fv(location,false,mat4.get(matBuffer));
        matBuffer.clear();
    }

    public void uploadInt(String varName, int x){
        int location = glGetUniformLocation(this.shaderProgram,varName);
        glUniform1i(location,x);
    }
    public void uploadFloat(String varName, float x){
        int location = glGetUniformLocation(this.shaderProgram,varName);
        glUniform1f(location,x);
        matBuffer.clear();
    }


    public void uploadVec3f(String varName, Vector3f vector){
        int location = glGetUniformLocation(this.shaderProgram,varName);
        glUniform3fv(location,vector.get(vec3fBuffer));
        vec3fBuffer.clear();
    }

    public void uploadVec2f(String varName, Vector2f vector){
        int location = glGetUniformLocation(this.shaderProgram,varName);
        glUniform2fv(location,vector.get(vec2fBuffer));
        vec2fBuffer.clear();
    }

    }
