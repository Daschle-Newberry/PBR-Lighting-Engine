#version 330 core
in vec2 UV;

uniform sampler2D screenTexture;
uniform sampler2D depthMap;


out vec4 outColor;

bool isEdge(){
    float fragDepth = texture(depthMap, UV).r;
    vec2 texelSize = vec2(1.0/2560, 1.0/1440);

    float averageDepth = 0.0;

    for (int y = -1; y <= 1; y++){
        for (int x = -1; x <= 1; x++){
            if (x == 0 && y ==0) continue;
            vec2 offset = vec2(x, y) * texelSize;
            averageDepth += texture(depthMap, UV.xy + offset).r;
        }
    }

    averageDepth /= 8;

    vec3 color;
    if (abs(averageDepth - fragDepth) > .000001f){
        return true;
    } else {
        return false;
    }
}
void main()
{
//    if(isEdge()){
//        outColor = vec4(1.0f);
//    }else{}
    outColor = texture(screenTexture, UV);
}