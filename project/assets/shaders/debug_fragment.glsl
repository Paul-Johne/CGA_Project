#version 330 core

// NO "in vec3 col" for "out vec4 color", since we're only using textures
uniform sampler2D diffPalette;
//uniform sampler2D diffWall;

uniform int shaderType;

out vec4 color;

in struct VertexData {
    vec3 position;
    vec2 textureCoordinates;
    vec3 normal;
} vertexData;

void main() {
    /* objects with different material in same shader => Trick: textureUnit must be the same */
    vec3 diffPaletteTerm = texture(diffPalette, vertexData.textureCoordinates).rgb;
    //vec3 diffWallTerm = texture(diffWall, vertexData.textureCoordinates).rgb;

    color += vec4(diffPaletteTerm, 1.0f); // 0.0f before Merge Kevin -> Paul
    //color += vec4(diffWallTerm, 1.0f); // -||-

    vec3 lum = vec3(0.299f, 0.587f, 0.114f);

    if(shaderType == 1){
        color = vec4(vec3(dot(color.rgb, lum)), color.a);
    }
}