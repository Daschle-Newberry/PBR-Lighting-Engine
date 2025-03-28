#version 460

layout(location = 0) out vec4 outColor;

uniform vec3 lightDirection;

in vec3 fragNormal;
in vec2 UV;

void main(){
    outColor = vec4(normalize(vec3(dot(lightDirection,fragNormal))),1.0f);
}