#version 330
out vec2 UV;


vec4 vertices[] = vec4[6](
    vec4(-1.0f,  1.0f,  0.0f, 1.0f),
    vec4(-1.0f, -1.0f,  0.0f, 0.0f),
    vec4(1.0f, -1.0f,  1.0f, 0.0f),

    vec4(-1.0f,  1.0f,  0.0f, 1.0f),
    vec4(1.0f, -1.0f,  1.0f, 0.0f),
    vec4(1.0f,  1.0f,  1.0f, 1.0f)

);


void main()
{
    gl_Position = vec4(vertices[gl_VertexID].xy, 0.0, 1.0);
    UV = vertices[gl_VertexID].zw;
}

