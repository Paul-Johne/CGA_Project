#version 330 core
#extension GL_EXT_geometry_shader4 : enable
#define NUM_POINT_LIGHTS 1

/* Input-Type */
layout (triangles) in;
/* Output-Primitive-Type => strip allows more than one triangle | max_vertices defines amount of generated vertices per execution*/
layout (triangle_strip, max_vertices = 3) out;

/* in arrays contain 3 values (one for each vertex */
struct VertexData {
    vec3 position;
    vec2 texCoords;
    vec3 normal;
};
in VertexData vertexData[];
out VertexData vertexDataGeo;

/* Lighting - Stuff (using Interface Blocks) */
in vec3 toCamera[];
out vec3 toCameraGeo;

struct PointLight {
    vec3 position;
    vec3 color;
    vec3 attenuation;
};
uniform PointLight pointLights[NUM_POINT_LIGHTS];
in vec3 toPointLights[][NUM_POINT_LIGHTS];
out vec3 toPointLightsGeo[NUM_POINT_LIGHTS];


/* Tangent and Bitangent for each vertex */
vec3 tangent;
vec3 biTangent;
/* Tangent Space for Fragment Shader */
out mat3 TBN;

vec3 calculateNormalizedTangent() {
    vec3 p0 = vertexData[0].position; // gl_in[0].gl_Position.xyz; => would be in Clipping Space, but we need World Space
    vec3 p1 = vertexData[1].position; // gl_in[1].gl_Position.xyz; => -||-
    vec3 p2 = vertexData[2].position; // gl_in[2].gl_Position.xyz; => -||-

    vec3 e1 = p1 - p0;
    vec3 e2 = p2 - p0;

    vec2 uv0 = vertexData[0].texCoords;
    vec2 uv1 = vertexData[1].texCoords;
    vec2 uv2 = vertexData[2].texCoords;

    vec2 deltaUV1 = uv1 - uv0;
    vec2 deltaUV2 = uv2 - uv0;

    float detM = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);
    return normalize( detM * (deltaUV2.y * e1 - deltaUV1.y * e2) );
}

void main() {
    /* calculating tangent and bitangent for each vertex of current triangle*/
    for (int i = 0; i < 3; i++) {
        tangent = calculateNormalizedTangent();
        biTangent = normalize(cross(vertexData[0].normal, tangent));

        TBN = mat3(tangent, biTangent, vertexData[0].normal);

        vertexDataGeo.position = vertexData[0].position;
        vertexDataGeo.texCoords = vertexData[0].texCoords;
        vertexDataGeo.normal = vertexData[0].normal;

        gl_Position = gl_in[i].gl_Position; // MOST IMPORTANT LINE OF CODE

        toCameraGeo = toCamera[0];
        toPointLightsGeo = toPointLights[0];

        EmitVertex();
    }

    EndPrimitive(); // next strip COULD be calculated
}