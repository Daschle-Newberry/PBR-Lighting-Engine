#version 330
layout(location=0) in vec3 aPos;
layout(location=1) in vec2 aUV;
layout(location=2) in vec3 aNormals;


uniform mat4 cameraProjectionMatrix;
uniform mat4 cameraViewMatrix;

uniform mat4 lightProjectionMatrix;
uniform mat4 lightViewMatrix;

uniform mat4 modelMatrix;

out vec3 frag_color;
out vec2 UV;
out vec3 fragNormal;
out vec4 fragLightSpacePos;
out vec3 fragPos;

void main() {
    gl_Position = cameraProjectionMatrix * cameraViewMatrix * modelMatrix * vec4(aPos,1.0);
    UV = aUV;

    fragNormal = aNormals;

    fragPos = vec3(modelMatrix * vec4(aPos,1.0));

    fragLightSpacePos = lightProjectionMatrix * lightViewMatrix * modelMatrix * vec4(aPos,1.0);
}
