#version 460 core

uniform sampler2D equirectangularMap;

const vec2 invAtan = vec2(0.1591, 0.3183);

in vec3 localPos;
out vec4 outColor;

vec2 sampleSphericalMap(vec3 v){
    vec2 UV = vec2(atan(v.z,v.x),asin(v.y));
    UV *= invAtan;
    UV += 0.5f;
    return UV;
}

void main() {
    vec2 UV = sampleSphericalMap(normalize(localPos));
    outColor = vec4(texture(equirectangularMap,UV).rgb,1.0);
}
