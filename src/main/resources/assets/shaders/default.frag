#version 420 core
uniform sampler2D albedo;

in vec2 UV;
out vec4 outColor;

void main(){
    outColor = texture(albedo,UV);
}