#version 420 core

in vec3 texCoords;

uniform samplerCube skyBox;

out vec4 outColor;
void main() {

    outColor = texture(skyBox,texCoords);
}
