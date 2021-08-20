#version 330 core
/* Adjust the amount according to used light in scene. */
#define NUM_POINT_LIGHTS 0

/* Vertexattributes of mesh's vertices */
layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoords;
layout (location = 2) in vec3 normal;

/* Matrices to reach clipping space for gl_Position */
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

out struct VertexData {
    vec3 position;
    vec2 texCoords;
    vec3 normal;
} vertexData;

uniform vec2 tcMultiplier;
out vec3 toCamera;

/* uniform will be uploaded with PointLight.bind() */
out struct PointLight {
    vec3 position;
    vec3 color;
    vec3 attenuation;
};
uniform PointLight pointLights[NUM_POINT_LIGHTS];
out vec3 toPointLights[NUM_POINT_LIGHTS];

void main() {
    vec4 pos = vec4(position, 1.0f); // ObjectSpace
    vec4 posWorldSpace = model_matrix * pos;
    vec4 posCameraSpace = view_matrix * posWorldSpace;

    /* will be used in geometry shader */
    gl_Position = projection_matrix * view_matrix * posWorldSpace;

    vertexData.position = posWorldSpace.xyz;
    vertexData.texCoords = tcMultiplier * textureCoordinates;
    vertexData.normal = (inverse(transpose(view_matrix * model_matrix)) * vec4(normal,0.0f)).xyz; // Normal in Camera Space

    toCamera = -posCameraSpace.xyz;

    for (int i = 0; i < pointLights.length; i++) {
        toPointLights[i] = (view_matrix * vec4(pointLights[i].position, 1.0f)).xyz + toCamera; // lightDir = (lp - P).xyz
    }
}