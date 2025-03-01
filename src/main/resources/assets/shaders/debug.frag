#version 420 core

/* RENDERTARGETS colortex0, colortex1, colortex2 */

uniform sampler2D color;

in vec2 UV;
in vec3 normal;

out vec4 outColor;

void main() {
    outColor = texture(color,UV);
}