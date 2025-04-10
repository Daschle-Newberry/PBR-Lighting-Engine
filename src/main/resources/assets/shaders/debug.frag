#version 460
layout(location = 0) out vec4 outColor;

uniform sampler2D albedo;

in vec3 fragNormal;
in vec2 UV;

void main(){
    outColor = texture(albedo,UV);
    outColor.rgb = pow(outColor.rgb, vec3(2.2f));
}