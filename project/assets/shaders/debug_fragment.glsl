#version 330 core

// NO "in vec3 col" for "out vec4 color", since we're only using textures
uniform sampler2D diffPalette;
uniform sampler2D diffWall;

out vec4 color;

in struct VertexData {
    vec3 position;
    vec2 textureCoordinates;
    vec3 normal;
} vertexData;

void main() {
    vec3 diffPaletteTerm = texture(diffPalette, vertexData.textureCoordinates).rgb;
    vec3 diffWall = texture(diffWall, vertexData.textureCoordinates).rgb;

    color += vec4(diffPaletteTerm, 0.0f);
}