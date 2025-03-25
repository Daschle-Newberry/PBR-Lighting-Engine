#version 420 core
uniform float gridCellSize = .025f;
uniform vec4 gridColorThin = vec4(0.5f,0.5f,0.5f,1.0f);
uniform vec4 gridColorThick = vec4(0.0f,0.0f,0.0f,1.0f);


in vec3 worldPosition;

out vec4 outColor;

void main() {
    float lod0A = mod(worldPosition.z,gridCellSize);
    vec4 color;

    color = gridColorThick;
    color.a *= lod0A;


    outColor = color;

}
