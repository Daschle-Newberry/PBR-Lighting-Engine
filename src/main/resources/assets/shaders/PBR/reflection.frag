#version 460 core
layout(location = 0) out vec4 fragColor;
uniform sampler2D albedo;

in vec2 UV;

void main() {
    fragColor = vec4(pow(texture(albedo,UV).rgb,vec3(2.2)),1.0f);
}
