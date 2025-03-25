#version 460 core
layout(location = 0) in vec3 aPos;

const vec3 vertices[] = vec3[8](
    vec3(-1, -1, -1),
    vec3(1, -1, -1),
    vec3(1, 1, -1),
    vec3(-1, 1, -1),
    vec3(-1, -1, 1),
    vec3(1, -1, 1),
    vec3(1, 1, 1),
    vec3(-1, 1, 1)
);
const int indices[] = int[36](
    0, 3, 1, 3, 2, 1,
    1, 2, 5, 2, 6, 5,
    5, 6, 4, 6, 7, 4,
    4, 7, 0, 7, 3, 0,
    3, 7, 2, 7, 6, 2,
    4, 0, 5, 0, 1, 5
    );

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 scaleMatrix;

out vec3 fragColor;

mat4 createTranslationMatrix(vec3 position){
    return mat4(
    1.0, 0.0, 0.0, 0.0,
    0.0, 1.0, 0.0, 0.0,
    0.0, 0.0, 1.0, 0.0,
    position.x, position.y, position.z, 1.0
    );
}

void main() {
    mat4 translationMatrix = createTranslationMatrix(aPos);
    gl_Position = projectionMatrix * viewMatrix * translationMatrix * scaleMatrix * vec4(vertices[indices[gl_VertexID]],1.0);
    fragColor = vertices[indices[gl_VertexID]];
}


