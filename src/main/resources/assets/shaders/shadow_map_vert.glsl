#version 330 core
layout(location=0) in vec3 aPos;
layout(location=1) in vec3 colors;
layout(location=2) in vec3 aNormals;

//Mesh
uniform mat4 modelMatrix;

//Light
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;



void main() {
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(aPos, 1.0);
}
