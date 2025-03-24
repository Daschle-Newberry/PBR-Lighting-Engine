#version 460

layout(location = 0) out vec4 outColor;

uniform sampler2D albedo;

in vec3 fragNormal;
in vec2 UV;

void main(){
    outColor = vec4(pow(texture(albedo,UV).rgb,vec3(2.2)),1.0f);
}