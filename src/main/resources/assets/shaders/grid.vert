#version 420 core

uniform vec3 cameraPosition;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;


const vec3 gridPlane[4] = vec3[4] (
    vec3(-1.0f,0.0f,-1.0f),
    vec3(1.0f,0.0f,-1.0f),
    vec3(1.0f,0.0f,1.0f),
    vec3(-1.0f,0.0f,1.0f)
);

const int indices[6] = int[6](0,2,1,2,0,3);

out vec3 worldPosition;

void main() {
    int index = indices[gl_VertexID];
    vec4 point = vec4(gridPlane[index],1.0f);
    point.x += cameraPosition.x;
    point.z += cameraPosition.z;

    worldPosition = point.xyz;

    gl_Position = projectionMatrix * viewMatrix * point;
}