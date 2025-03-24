#version 420 core

uniform mat4 cameraProjectionMatrix;
uniform mat4 cameraViewMatrix;

out vec3 texCoords;
out vec4 aPos;

vec3 vertices[] = vec3[8](
                    vec3(-1, -1, -1),
                    vec3(1, -1, -1),
                    vec3(1, 1, -1),
                    vec3(-1, 1, -1),
                    vec3(-1, -1, 1),
                    vec3(1, -1, 1),
                    vec3(1, 1, 1),
                    vec3(-1, 1, 1)
);
int indices[] = int[36](
                    0, 1, 3, 3, 1, 2,
                    1, 5, 2, 2, 5, 6,
                    5, 4, 6, 6, 4, 7,
                    4, 0, 7, 7, 0, 3,
                    3, 2, 7, 7, 2, 6,
                    4, 5, 0, 0, 5, 1
);


void main() {
    aPos = vec4(vertices[indices[gl_VertexID]],1.0);
    vec4 pos = cameraProjectionMatrix * cameraViewMatrix * aPos;
    gl_Position = vec4(pos.x,pos.y,pos.w,pos.w);
    texCoords = vec3(aPos.x,aPos.y,-aPos.z);

}
