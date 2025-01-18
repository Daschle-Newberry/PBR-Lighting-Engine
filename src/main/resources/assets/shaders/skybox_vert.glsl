#version 420 core
layout(location=0) in vec3 aPos;

uniform mat4 cameraProjectionMatrix;
uniform mat4 cameraViewMatrix;

out vec3 texCoords;

void main() {

    vec4 pos = cameraProjectionMatrix * cameraViewMatrix * vec4(aPos,1.0);
    gl_Position = vec4(pos.x,pos.y,pos.w,pos.w);
    texCoords = vec3(aPos.x,aPos.y,-aPos.z);

}
