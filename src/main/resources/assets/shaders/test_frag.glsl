#version 420 core
#define depthFilterSize 9
uniform sampler2D depthMap;
uniform vec2 depthMapDimensions;

in vec2 UVCoords;
out vec4 outColor;

void main() {
    float pixelDepth = texture(depthMap, UVCoords).r;
    vec2 texelSize = vec2(1.0/depthMapDimensions.x,1.0/depthMapDimensions.y);

    float depth;
    float averageDepth = 0.0;
    for (int y = -depthFilterSize/2; y <= -depthFilterSize/2 + depthFilterSize; y++){
        for (int x = -depthFilterSize/2; x <= -depthFilterSize/2 + depthFilterSize; x++){
            if(x == 0 && y ==0) continue;
            vec2 offset = vec2(x,y) * texelSize;
            depth = texture(depthMap, UVCoords.xy + offset).r;
            averageDepth += depth;
        }
    }

    averageDepth /= depthFilterSize - 1;

    vec3 pixelColor;
    if(abs(averageDepth - pixelDepth) > 3.5f){
        pixelColor = vec3(1.0f);
    }
    else{
        pixelColor = vec3(0.0f);
    }

    outColor = vec4(pixelColor,1.0f);
}
