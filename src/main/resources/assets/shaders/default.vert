#version 420 core
layout(location=0) in vec3 aPos;
layout(location=1) in vec2 aUV;
layout(location=2) in vec3 aNormals;
layout(location=3) in vec3 aTangents;

uniform mat4 cameraProjectionMatrix;
uniform mat4 cameraViewMatrix;
uniform mat4 lightProjectionMatrix;
uniform mat4 lightViewMatrix;
uniform mat4 modelMatrix;


out mat3 TBN;
out vec4 fragLightSpacePos;
out vec3 fragNormal;
out vec3 fragPos;
out vec2 UV;





void main() {
    gl_Position = cameraProjectionMatrix * cameraViewMatrix * modelMatrix * vec4(aPos,1.0);

    UV = vec2(aUV.s,-aUV.t);
    fragNormal = aNormals;

    vec3 T = normalize(vec3(modelMatrix * vec4(aTangents,0.0)));
    vec3 N = normalize(vec3(modelMatrix * vec4(aNormals,0.0)));
    vec3 B = cross(N,T);
    mat3 tangentBiTangent = mat3(T, B, N);
    TBN = tangentBiTangent;

    fragPos = vec3(modelMatrix * vec4(aPos,1.0));
    fragLightSpacePos = lightProjectionMatrix * lightViewMatrix * modelMatrix * vec4(aPos,1.0);
}
