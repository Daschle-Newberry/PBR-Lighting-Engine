#version 420 core

#define ambientStrength .4
#define shadowFilterSize 16
uniform sampler2D textureImg;
uniform sampler2D shadowMap;

uniform int isTextured;



uniform DirectionalLight sun;
uniform vec2 shadowMapDimensions;
uniform vec3 cameraPosition;

in vec2 UV;
in vec3 fragNormal;
in vec4 fragLightSpacePos;
in vec3 fragPos;

out vec4 outColor;


float calcShadowFactor(){
    vec3 projCoords = fragLightSpacePos.xyz/fragLightSpacePos.w;
    vec3 UVCoords = projCoords * .5 + vec3(.5);

    float diffuseFactor = dot(fragNormal,-sun.direction);
    float bias = mix(0.005,0.0f,diffuseFactor);

    vec2 texelSize = vec2(1.0/shadowMapDimensions.x,1.0/shadowMapDimensions.y);

    float depth = 0.0f;
    float sum = 0.0f;
    for (int y = -shadowFilterSize/2; y <= -shadowFilterSize/2 + shadowFilterSize; y++){
        for (int x = -shadowFilterSize/2; x <= -shadowFilterSize/2 + shadowFilterSize; x++){
            vec2 offset = vec2(x,y) * texelSize;
            depth = texture(shadowMap, UVCoords.xy + offset).r;
            if(depth + bias < UVCoords.z){
                sum += 0.0f;
            }
            else{
                sum += 1.0f;
            }
        }
    }
    return sum/float(pow(shadowFilterSize,2));
}

vec3 calcDiffuseLight(){
    vec3 norm = normalize(fragNormal);
    float diff = max(dot(norm,normalize(sun.direction)),0.0);
    vec3 diffuse = diff * sun.color;
    return diffuse;
}

vec3 calcDirectionalLight(){
    vec3 ambient = ambientStrength * sun.color;
    vec3 diffuse = calcDiffuseLight();
    float shadowFactor = calcShadowFactor();
    return (shadowFactor * diffuse) + ambient;

}


void main() {
    vec4 color;
    if (isTextured == 0){
        color = vec4(0.0f,.4f,.4f,1.0f);
    }
    else{
        color = vec4(.5f,0.0f,.5f,1.0f);
    }

    vec3 finalLighting = calcDirectionalLight();

    outColor = vec4(vec4(finalLighting,1.0) * color);

}
