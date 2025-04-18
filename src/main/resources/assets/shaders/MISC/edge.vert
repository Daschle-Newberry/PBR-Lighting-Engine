#version 420 core
layout(location=0) in vec3 aPos;
layout(location=1) in vec2 aUV;

uniform mat4 cameraProjectionMatrix;
uniform mat4 cameraViewMatrix;
uniform mat4 modelMatrix;

out vec2 UV;

void main() {
    gl_Position = cameraProjectionMatrix * cameraViewMatrix * modelMatrix * vec4(aPos,1.0);

    UV = vec2(aUV.s,aUV.t);
}
