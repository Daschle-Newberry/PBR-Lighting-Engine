#version 330 core
uniform sampler2D textureIMG;
uniform sampler2D shadowMap;
uniform int isTextured;

uniform vec3 sunDirection;



in vec2 UV;
in vec3 fragNormal;
in vec4 fragLightSpacePos;
in vec3 fragPos;

out vec4 outColor;

float ambientStrength = .4;
vec3 lightColor =  vec3(1.0);


float calcShadowFactor(){
    vec3 projCoords = fragLightSpacePos.xyz/fragLightSpacePos.w;
    vec2 UVCoords = vec2(0.5 * projCoords.x + 0.5, 0.5 * projCoords.y + 0.5);

    float z = 0.5 * projCoords.z + 0.5;

    float bias = max(0.05 * (1.0 - dot(fragNormal,normalize(sunDirection))),0.005);
    float depth = texture(shadowMap, UVCoords.xy).r;

    if(depth + bias < z){
        return 0.0f;
    }
    else{
        return 1.0f;
    }
}
vec3 calcDiffuseLight(){
    vec3 norm = normalize(fragNormal);
    float diff = max(dot(norm,normalize(sunDirection)),0.0);
    vec3 diffuse = diff * lightColor;
    return diffuse;
}

vec3 calcDirectionalLight(){
    vec3 ambient = ambientStrength * lightColor;
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
