#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoordinates;
layout(location = 2) in vec3 normal;

uniform mat4 model_matrix; // Object to World
uniform mat4 view_matrix; // World to Camera
uniform mat4 projection_matrix; // Camera to Clipping

out struct VertexData {
    vec3 position;
    vec2 textureCoordinates;
    vec3 normal;
} vertexData;

uniform vec2 tcMultiplier;
out vec3 toCamera;

void main() {
    vec4 pos = vec4(position, 1.0f);
    vec4 posWorldSpace = model_matrix * pos;
    vec4 posCameraSpace = view_matrix * posWorldSpace;

    gl_Position = projection_matrix * view_matrix * posWorldSpace; // actual position of single vertex

    vertexData.position = posWorldSpace.xyz;
    vertexData.textureCoordinates = tcMultiplier * textureCoordinates; // how stretched the texture will appear in fragment shader
    vertexData.normal = (inverse(transpose(view_matrix * model_matrix)) * vec4(normal,0.0f)).xyz; // Normals to Camera Space

    toCamera = -posCameraSpace.xyz; // for light calculation in fragment shader
}