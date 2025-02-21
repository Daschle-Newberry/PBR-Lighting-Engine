#version 420 core

#define MAX_LIGHTS 4
#define pi 3.1415926535897932384626433832795
#define ambientStrength .03
#define shadowFilterSize 16

struct PBRMaterial
{
    float roughness;
    float metallic;
    vec3 albedo;
};

struct Light {
    vec3 color;
    vec3 positionDirection;
    float intensity;
    bool isDirectional;
};

struct Sun{
    vec3 direction;
};

uniform sampler2D albedo;
uniform sampler2D normalMap;
uniform sampler2D metallic;
uniform sampler2D roughness;
uniform sampler2D AO;

uniform sampler2D shadowMap;


uniform Light lights[MAX_LIGHTS];
uniform vec2 shadowMapDimensions;
uniform vec3 cameraPos;


in mat3 TBN;
in vec4 fragLightSpacePos;
in vec3 fragNormal;
in vec3 fragPos;
in vec2 UV;


out vec4 outColor;


PBRMaterial material = PBRMaterial(texture(roughness,UV).r,texture(metallic,UV).r,texture(albedo,UV).rgb);
Sun sun;

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

float distributionGGX(float nDh){
    float alpha2 = pow(material.roughness,4.0f);
    float denominator = pow(nDh,2.0f) * (alpha2 - 1.0f) + 1.0f;
    denominator = pi * pow(denominator,2.0f);
    return alpha2/max(denominator,0.000001f);
}

float geometryShadowSmith(float dp){
    float r = material.roughness + 1.0f;
    float k = pow(r,2.0f)/8.0f;
    float denominator = dp * (1.0f - k) + k;
    return dp/denominator;
}

vec3 fresnelSchlick(vec3 F0,float vDh){
    vec3 ret = F0 + (1.0f - F0) * pow(1.0 - vDh,5.0f);
    return ret;
}

vec3 calculateTBTNormals(){
    vec3 normal = texture(normalMap, UV).rgb;
    normal = normal * 2.0f - 1.0f;
    return normalize(TBN*normal);
}

float calculateAttenuationQuadratic(vec3 fragPos, vec3 lightPosition){
    float distance = length(lightPosition - fragPos);
    return 1.0/(distance*distance);
}

vec3 calculatePBRLighting(Light light){


    vec3 baseReflectivity = mix(vec3(0.04),material.albedo,material.metallic);
    vec3 lightDirection = light.positionDirection;
    vec3 radiance = light.color * light.intensity;
    if(!light.isDirectional){
        lightDirection = light.positionDirection - fragPos;
        radiance *= calculateAttenuationQuadratic(fragPos,light.positionDirection);
    }

    vec3 normal = calculateTBTNormals();
    vec3 N = normalize(normal);
    vec3 V = normalize(cameraPos - fragPos);
    vec3 L = normalize(lightDirection);
    vec3 H = normalize(V + L);

    float nDh = dot(N,H);
    float nDv = max(dot(N,V),0.000001f);
    float nDl = max(dot(N,L),0.000001f);
    float vDh = dot(V,H);


    float D = distributionGGX(nDh);
    float G1 = geometryShadowSmith(nDv);
    float G2 = geometryShadowSmith(nDl);
    float GG = G1 * G2;
    vec3 F = fresnelSchlick(baseReflectivity,vDh);

    vec3 specular = D * GG * F;

    specular /= (4.0f * nDv * nDl);

    vec3 kD = vec3(1.0f) - F;

    kD *= 1.0 - material.metallic;


    return (kD * material.albedo/pi + specular) * radiance * nDl;


}
void main() {
    vec3 outputLum = vec3(0);
    for(int i = 0; i < MAX_LIGHTS; i++){
        outputLum += calculatePBRLighting(lights[i]);
    }


    outputLum = outputLum / (outputLum + vec3(1.0));
    outputLum = pow(outputLum,vec3(1.0/2.2));

    float shadowFactor = calcShadowFactor();
    outColor = vec4(shadowFactor * outputLum,1.0);
}
