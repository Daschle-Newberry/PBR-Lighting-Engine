#version 420 core

#define MAX_LIGHTS 1
#define pi 3.1415926535897932384626433832795
#define ambientStrength .03
#define shadowFilterSize 16


struct PBRMaterial
{
    float roughness;
    float metallic;
    float AO;
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

uniform bool useORM;
uniform sampler2D albedo;
uniform sampler2D normalMap;
uniform sampler2D metallicORM;
uniform sampler2D roughness;
uniform sampler2D AO;

uniform samplerCube irradianceMap;
uniform samplerCube specularMap;
uniform sampler2D brdfLUT;

uniform sampler2D shadowMap;
uniform vec2 shadowMapDimensions;

uniform Light lights[MAX_LIGHTS];
uniform vec3 cameraPosition;


in mat3 TBN;
in vec4 fragLightSpacePos;
in vec3 fragNormal;
in vec3 fragPos;
in vec2 UV;


out vec4 outColor;

PBRMaterial material;
Sun sun;

float calcShadowFactor(){
    vec3 projCoords = fragLightSpacePos.xyz/fragLightSpacePos.w;
    vec3 UVCoords = projCoords * .5 + vec3(.5);
    float diffuseFactor = dot(fragNormal,-sun.direction);
    float bias = mix(0.005,0.0f,diffuseFactor);

    vec2 texelSize = vec2(1.0/shadowMapDimensions.x,1.0/shadowMapDimensions.y);

    float sum = 0.0f;
    float depth = 0.0f;

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
vec3 calculateTBTNormals(){
    vec3 normal = texture(normalMap, UV).rgb;
    normal = normal * 2.0f - 1.0f;
    return normalize(TBN*normal);
}

float distributionGGX(vec3 N, vec3 H){
    float nDh = max(0.0,dot(N,H));
    float alpha2 = pow(material.roughness,4.0f);
    float denominator = max(0.000000001,pow(nDh,2.0f) * (alpha2 - 1.0f) + 1.0f);
    denominator = pi * pow(denominator,2.0f);
    return alpha2/max(denominator,0.000001f);
}

float geometryGGX(vec3 D, vec3 N, float K){
    float dp = max(0.0,dot(D,N));
    float numerator = 2.0 * (dp);
    float denominator = dp + sqrt(K + (1 - K) * pow(dp,2.0));
    return numerator/denominator;
}

float geometryShadowing(vec3 N, vec3 L, vec3 V, float K){
    float G1 = geometryGGX(L,N,K);
    float G2 = geometryGGX(V,N,K);

    return G1 * G2;
}

vec3 fresnelSchlick(vec3 H, vec3 V, vec3 F0){
    float hDv = max(0.0,dot(H,V));
    return F0 + (1 - F0) * pow((1-(hDv)),5.0);
}
vec3 fresnelShlickFromRoughness(float cosTheta, vec3 F0, float roughness)
{
    return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}


vec3 FLambert(){
    return material.albedo/pi;
}

float calculateAttenuationQuadratic(vec3 fragPos, vec3 lightPosition){
    float distance = length(lightPosition - fragPos);
    return 1.0/(distance*distance);
}

vec3 CookTorrenceBRDF(Light light, vec3 N, vec3 V,vec3 F0){
    vec3 lightDirection;
    vec3 radiance = light.color * light.intensity;
    if(!light.isDirectional){
        lightDirection = light.positionDirection - fragPos;
        radiance *= calculateAttenuationQuadratic(fragPos,light.positionDirection);
    }else{
        lightDirection = -light.positionDirection;
    }

    float K = pow((material.roughness + 1.0),2.0)/8.0;

    vec3 L = normalize(lightDirection);
    vec3 H = normalize(V + L);


    float G = geometryShadowing(N,L,V,K);
    float D = distributionGGX(N,H);
    vec3 kS = fresnelSchlick(H,V,F0);
    vec3 specular = D * kS * G;
    specular /= 4 * (dot(N,V) * (dot(N,L))) + .000001;

    vec3 kD = vec3(1.0) - kS;
    kD *= 1.0 - material.metallic;


    return (kD * material.albedo/pi + specular) * radiance * max(0.0,dot(N,L));
}
vec3 calculateIBL(vec3 N, vec3 V,vec3 F0){
    // Specular part of this function is heavily influenced by learnopengl.com/PBR/IBL/Specular-IBL
    vec3 kS = fresnelSchlick(N,V,F0);
    vec3 kD = vec3(1.0) - kS;
    kD *= 1.0 - material.metallic;

    vec3 irradiance = texture(irradianceMap, N).rgb;
    vec3 diffuse = irradiance * material.albedo/pi;

    const float MAX_REFLECTION_LOD = 4.0;

    vec3 reflectionVector = reflect(-V,N);
    vec3 F = fresnelShlickFromRoughness(max(dot(N, V), 0.0), F0, material.roughness);

    vec3 prefilteredColor = textureLod(specularMap, reflectionVector, material.roughness * MAX_REFLECTION_LOD).rgb;
    vec2 precalculatedBRDF = texture(brdfLUT,vec2(max(dot(N,V),0.0),material.roughness)).rg;
    vec3 specular = prefilteredColor * (F * precalculatedBRDF.x + precalculatedBRDF.y);
    return (kD * diffuse + specular) * material.AO;
}
void main() {
    if(!useORM){
        material = PBRMaterial(texture(roughness,UV).r,texture(metallicORM,UV).r,texture(AO,UV).a,pow(texture(albedo,UV).rgb,vec3(2.2f)));
    }else{
        material = PBRMaterial(texture(metallicORM,UV).g,texture(metallicORM,UV).b,texture(AO,UV).a,pow(texture(albedo,UV).rgb,vec3(2.2f)));
    }

    vec3 F0 = vec3(0.04f);
    F0 = vec3(mix(F0,material.albedo,material.metallic));
    vec3 N = normalize(calculateTBTNormals());
    vec3 V = normalize(cameraPosition - fragPos);

    vec3 outputLum = vec3(0.0f);
    for(int i = 0; i < MAX_LIGHTS; i++){
        outputLum += CookTorrenceBRDF(lights[i],N,V,F0);
    }

    vec3 ambient = calculateIBL(N,V,F0);
    float shadowFactor = calcShadowFactor();
    outColor = vec4((outputLum * shadowFactor) + ambient,1.0);

}
