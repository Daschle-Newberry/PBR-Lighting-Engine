#version 330 core

layout(location = 0) out vec4 finalColor;

uniform sampler2D colortex0;

in vec2 UV;

void main()
{
    finalColor = texture(colortex0,UV);
    finalColor.rgb = finalColor.rgb / (finalColor.rgb + vec3(1.0));
    finalColor.rgb = pow(finalColor.rgb,vec3(1.0/2.2));
}