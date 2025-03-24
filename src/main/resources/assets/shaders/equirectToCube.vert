#version 460 core
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

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

out vec3 localPos;

void main() {
    localPos = vertices[indices[gl_VertexID]];
    gl_Position = projectionMatrix * viewMatrix * vec4(localPos,1.0f);
}
