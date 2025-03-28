#version 330 core

/*
 THIS CODE IS FROM learnopengl.com/PBR/IBL/Specular-IBL
*/

layout(location = 0) out vec2 fragColor;

in vec2 UV;

const float PI = 3.14159265359;

float RadicalInverse_VdC(uint bits)
{
    bits = (bits << 16u) | (bits >> 16u);
    bits = ((bits & 0x55555555u) << 1u) | ((bits & 0xAAAAAAAAu) >> 1u);
    bits = ((bits & 0x33333333u) << 2u) | ((bits & 0xCCCCCCCCu) >> 2u);
    bits = ((bits & 0x0F0F0F0Fu) << 4u) | ((bits & 0xF0F0F0F0u) >> 4u);
    bits = ((bits & 0x00FF00FFu) << 8u) | ((bits & 0xFF00FF00u) >> 8u);
    return float(bits) * 2.3283064365386963e-10; // / 0x100000000
}
vec2 Hammersley(uint i, uint N)
{
    return vec2(float(i)/float(N), RadicalInverse_VdC(i));
}
vec3 ImportanceSampleGGX(vec2 Xi, vec3 N, float roughness)
{
    float a = roughness*roughness;

    float phi = 2.0 * PI * Xi.x;
    float cosTheta = sqrt((1.0 - Xi.y) / (1.0 + (a*a - 1.0) * Xi.y));
    float sinTheta = sqrt(1.0 - cosTheta*cosTheta);

    vec3 H;
    H.x = cos(phi) * sinTheta;
    H.y = sin(phi) * sinTheta;
    H.z = cosTheta;

    vec3 up          = abs(N.z) < 0.999 ? vec3(0.0, 0.0, 1.0) : vec3(1.0, 0.0, 0.0);
    vec3 tangent   = normalize(cross(up, N));
    vec3 bitangent = cross(N, tangent);

    vec3 sampleVec = tangent * H.x + bitangent * H.y + N * H.z;
    return normalize(sampleVec);
}

/*
 THIS CODE IS FROM learnopengl.com/PBR/IBL/Specular-IBL
*/


float geometryGGX(vec3 D, vec3 N, float K){
    float dp = max(0.0,dot(D,N));
    K = (K * K) / 2.0f;
    float numerator = (dp);
    float denominator = dp + K + (1 - K) * pow(dp,2.0);
    return numerator/denominator;
}

float geometryShadowing(vec3 N, vec3 V, vec3 L, float K){
    float G1 = geometryGGX(L,N,K);
    float G2 = geometryGGX(V,N,K);

    return G1 * G2;
}
// ----------------------------------------------------------------------------
vec2 integrateBRDF(float nDv, float roughness)
{
    vec3 V;
    V.x = sqrt(1.0 - nDv*nDv);
    V.y = 0.0;
    V.z = nDv;

    float A = 0.0;
    float B = 0.0;

    vec3 N = vec3(0.0, 0.0, 1.0);

    const uint SAMPLE_COUNT = 1024u;
    for(uint i = 0u; i < SAMPLE_COUNT; ++i)
    {

        vec2 Xi = Hammersley(i, SAMPLE_COUNT);
        vec3 H = ImportanceSampleGGX(Xi, N, roughness);
        vec3 L = normalize(2.0 * dot(V, H) * H - V);

        float nDl = max(L.z, 0.0);
        float nDh = max(H.z, 0.0);
        float vDh = max(dot(V, H), 0.0);

        if(nDl > 0.0)
        {
            float G = geometryShadowing(N, V, L, roughness);
            float G_Vis = (G * vDh) / (nDh * nDv);
            float Fc = pow(1.0 - vDh, 5.0);

            A += (1.0 - Fc) * G_Vis;
            B += Fc * G_Vis;
        }
    }
    A /= float(SAMPLE_COUNT);
    B /= float(SAMPLE_COUNT);
    return vec2(A, B);
}
// ----------------------------------------------------------------------------
void main()
{
    vec2 integratedBRDF = integrateBRDF(UV.x,UV.y);
    fragColor = integratedBRDF;
}

/*
 THIS CODE IS FROM learnopengl.com/PBR/IBL/Specular-IBL
*/