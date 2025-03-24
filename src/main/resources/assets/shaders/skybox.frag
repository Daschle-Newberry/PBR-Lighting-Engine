#version 420 core

in vec3 texCoords;
in vec4 aPos;

uniform samplerCube skybox;

out vec4 outColor;
void main() {

    outColor = textureLod(skybox, aPos.xyz, 1.2f);
    outColor.rgb = pow(outColor.rgb,vec3(2.2f));
}
