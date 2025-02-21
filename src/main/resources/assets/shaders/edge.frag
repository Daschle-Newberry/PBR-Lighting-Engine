#version 420 core
#define depthFilterSize 9

uniform sampler2D depthMap;
uniform vec2 depthMapDimensions;

in vec2 UV;
out vec4 outColor;

void main() {
    float fragDepth = texture(depthMap, UV).r;
    vec2 texelSize = vec2(1.0/depthMapDimensions.x,1.0/depthMapDimensions.y);

    float averageDepth = 0.0;

    for (int y = -1; y <= 1; y++){
        for (int x = -1; x <= 1; x++){
            if(x == 0 && y ==0) continue;
            vec2 offset = vec2(x,y) * texelSize;
            averageDepth += texture(depthMap, UV.xy + offset).r;
        }
    }

    averageDepth /= 8;

    vec3 color;
    if(abs(averageDepth - fragDepth) > .0001f){
        color = vec3(1.0f);
    }else{
        color = vec3(0.0f);
    }

    outColor = vec4(color,1.0f);
}
