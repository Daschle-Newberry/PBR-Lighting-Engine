#version 420

#define pi 3.1415926535897932384626433832795
#define lightColor vec3(1.0)
#define ambientStrength .4
#define shadowFilterSize 16

struct DirectionalLight
{
    vec3 color;
    vec3 direction;
    vec3 intensity;
};

struct PBRMaterial
{
    float roughness;
    float metallic;
    vec3 color;
};


uniform sampler2D textureImg;
uniform sampler2D shadowMap;
uniform int isTextured;

uniform DirectionalLight sun;
uniform vec2 shadowMapDimensions;
uniform vec3 cameraPos;

uniform PBRMaterial material;

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

float calculateNormalDistribution(float nDh){
    float alpha2 = material.roughness * material.roughness * material.roughness * material.roughness;
    float denominator = (nDh*nDh) * (alpha2 - 1) + 1;
    return alpha2/(denominator * denominator);
}

float calculateGeometryShadowing(float dp){
    float k = ((material.roughness + 1)*(material.roughness + 1))/8.0f;
    return dp/(dp * (1-k) + k);
}

vec3 calculateFresnel(vec3 F0,float vDh){

    vec3 ret = F0 + (1-F0) * pow(1.0-max(vDh,0.0),5);

    return ret;
}


vec3 calculatePBRLighting(DirectionalLight light){
    vec3 lightIntensity = light.color * light.intensity;

    vec3 lightDirection = light.direction;
    vec3 fragToCamera = normalize(cameraPos - fragPos);
    vec3 halfVector = normalize(fragToCamera + lightDirection);
    vec3 normal = normalize(fragNormal);

    float nDh = max(dot(normal,halfVector),0.0);
    float nDv = max(dot(normal,fragToCamera),0.0f);
    float nDl = max(dot(normal,lightDirection),0.0f);
    float vDh = dot(fragToCamera,halfVector);


    float D = calculateNormalDistribution(nDh);
    float G1 = calculateGeometryShadowing(nDv);
    float G2 = calculateGeometryShadowing(nDl);

    vec3 F0 = material.color;
    vec3 F = calculateFresnel(F0,vDh);

    float denominator = 4 * nDv * nDl + .0001f;
    vec3 specularBRDF = (D * (G1*G2) * F)/denominator;

    vec3 diffuseBRDF = (vec3(1.0) - F) * (1.0 - material.metallic);

    vec3 lambert = material.color/pi;

    vec3 finalColor = ((diffuseBRDF * lambert) + specularBRDF) * lightIntensity * nDl;

    return finalColor;



}
void main() {
    outColor = vec4(calculatePBRLighting(sun),1.0);
}
