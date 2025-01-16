#version 330 core
layout(location=0) in vec3 aPos;
layout(location=1) in vec3 colors;
layout(location=2) in vec3 aNormals;

uniform mat4 modelMatrix;
uniform mat4 cameraProjectionMatrix;
uniform mat4 cameraViewMatrix;

void main() {

    gl_Position = cameraProjectionMatrix * cameraViewMatrix * modelMatrix * vec4(aPos,1.0);

}
