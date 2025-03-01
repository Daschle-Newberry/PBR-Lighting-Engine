package OpenGL_Basic.renderer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL30.*;

public class Shader {
    private static ArrayList<String> usedColorTextures = new ArrayList<>();
    private static ArrayList<String> usedDepthTextures = new ArrayList<>();

    public boolean isCompiled = false;
    private int vertexID,fragmentID,shaderProgram;
    private String vertexShaderSrc,fragmentShaderSrc;
    private String vertex_path, fragment_path;

    private UniformBlock uniforms;
    private String[] outputBuffers;

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
            throw new RuntimeException("Error: Could not open file : '" + vert_path + "'");
        }


        InputStream fragmentStream = Shaders.class.getResourceAsStream(fragment_path);

        try {
            fragmentShaderSrc = new String(fragmentStream.readAllBytes(), StandardCharsets.UTF_8);
        }catch(IOException e){
            e.printStackTrace();
            throw new RuntimeException("Error: Could not open file : '" + frag_path + "'");
        }

        //Compile and link shaders
        //Vertex Shader
        System.out.println("Compiling Vertex Shader " + vertex_path);
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexShaderSrc);

        glCompileShader(vertexID);

        //Check for errors

        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);

        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            throw new RuntimeException("Error" + vertex_path +
                    "\n\tVertex shader compiliation failed" +
                    "\n" + glGetShaderInfoLog(vertexID, len));
        }
        System.out.println("Success");

        //Fragment Shader
        System.out.println("Compiling Fragment Shader " + fragment_path);
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentShaderSrc);

        glCompileShader(fragmentID);

        //Check for errors

        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);

        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);

            throw new RuntimeException("Error " + fragment_path +
                    "\n\tFragment shader compiliation failed" +
                    "\n" + glGetShaderInfoLog(fragmentID, len));
        }
        System.out.println("Success");

        uniforms = new UniformBlock();
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

            throw new RuntimeException("Error: Program:\n\t" +
                    vertex_path +
                    "\n\t" +
                    fragment_path +
                    "\n Shader compilation failed" +
                    '\n' + glGetProgramInfoLog(shaderProgram, len));

        }
        System.out.println("Success");
        getUniforms();
        getRenderTargets();
        isCompiled = true;
    }

    public void use(){
        glUseProgram(shaderProgram);
    }
    public void detach(){
        glUseProgram(0);
    }

    public int getID(){return this.shaderProgram;}
    public void getUniforms(){
        IntBuffer totalUniforms = BufferUtils.createIntBuffer(1);
        IntBuffer maxLength = BufferUtils.createIntBuffer(1);
        glGetProgramiv(shaderProgram,GL_ACTIVE_UNIFORMS, totalUniforms);
        glGetProgramiv(shaderProgram,GL_ACTIVE_UNIFORM_MAX_LENGTH, maxLength);

        System.out.println(totalUniforms.get(0));

        IntBuffer length = BufferUtils.createIntBuffer(1);
        IntBuffer size = BufferUtils.createIntBuffer(1);
        IntBuffer type = BufferUtils.createIntBuffer(1);
        ByteBuffer name = BufferUtils.createByteBuffer(maxLength.get());


        ArrayList<String> vec3Uniforms = new ArrayList<>();
        ArrayList<String> mat4Uniforms = new ArrayList<>();
        ArrayList<String> sampler2DUniforms = new ArrayList<>();
        ArrayList<String> intUniforms = new ArrayList<>();
        ArrayList<String> floatUniforms = new ArrayList<>();

        for(int i = 0; i < totalUniforms.get(0); i++) {

            glGetActiveUniform(shaderProgram,i,length,size,type,name);
            String nameString = StandardCharsets.UTF_8.decode(name.slice(0,length.get(0))).toString();

            if(nameString.equals("modelMatrix")) continue;

            switch(type.get(0)){
                case GL_FLOAT : floatUniforms.add(nameString);
                                    break;
                case GL_INT : intUniforms.add(nameString);
                                    break;
                case GL_SAMPLER_2D : sampler2DUniforms.add(nameString);
                                    break;
                case GL_FLOAT_MAT4 : mat4Uniforms.add(nameString);
                                    break;
                case GL_FLOAT_VEC3: vec3Uniforms.add(nameString);
                                    break;
                default : throw new RuntimeException("Unknown uniform type " + type.get(0) + " in program: \n" +
                        vertex_path +
                        "\n" +  fragment_path);
            }

        }

        uniforms.vec3f = new String[vec3Uniforms.size()];
        uniforms.mat4f = new String[mat4Uniforms.size()];
        uniforms.sampler2D = new String[sampler2DUniforms.size()];
        uniforms.Integer = new String[intUniforms.size()];
        uniforms.Float = new String[floatUniforms.size()];

        vec3Uniforms.toArray(uniforms.vec3f);
        mat4Uniforms.toArray(uniforms.mat4f);
        sampler2DUniforms.toArray(uniforms.sampler2D);
        intUniforms.toArray(uniforms.Integer);
        floatUniforms.toArray(uniforms.Float);
    }
    public void getRenderTargets(){

        String regex = "/\\* RENDERTARGETS (.*?) \\*/";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fragmentShaderSrc);

        if(matcher.find()){
            String targets = matcher.group(1);
            outputBuffers = targets.split(", ");

            for(String s : outputBuffers){
                System.out.println(s);
            }
        }else{
            outputBuffers = null;
        }
    }

    public UniformBlock getUniformBlock(){return this.uniforms;}

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
