#version 330 core
#define NUM_POINT_LIGHTS 1

uniform sampler2D diffWall;
uniform sampler2D normWall;
uniform sampler2D specWall;

uniform int shaderType;
vec3 lum = vec3(0.299f, 0.587f, 0.114f);

struct VertexData {
    vec3 position;
    vec2 texCoords;
    vec3 normal;
};
in VertexData vertexDataGeo;

in vec3 toCameraGeo;
vec3 normalizedToCamera;

struct PointLight {
    vec3 position;
    vec3 color;
    vec3 attenuation;
};
uniform PointLight pointLights[NUM_POINT_LIGHTS];
in vec3 toPointLightsGeo[NUM_POINT_LIGHTS];

vec3 normalizedToPointLight_current, normalizedReflectedToPointLight_current, normalizedHalfwayDirection_current;
float cosAlpha_current, cosBetaK_current, cosBetaK_Halfway_current;

/* option 1 - used */
in mat3 TBN;
/* option 2 - unused */
//mat3 invTNB = transpose(TBN); // transpose of orthogonal matrix equals its inverse

float getCosAngle(vec3 normalizedVector1, vec3 normalizedVector2) {
    return max(0.0f, dot(normalizedVector1, normalizedVector2));
}

float getCosAngleK(float cosAngle) {
    return pow(cosAngle, 50.0f); //cosAngle^shininess
}

vec3 brdfWall(vec3 diffTerm, vec3 specTerm, float cosAlpha, float cosBetaK, vec3 lightColor, vec3 toLight) {
    return (diffTerm * cosAlpha + specTerm * cosBetaK) * lightColor;
}

vec3 rgbToNormalizedXyz(vec3 normTerm) {
    return normalize(normTerm * 2.0f - 1.0f);
}

/* Fragment Shader Output */
out vec4 color;

void main() {
    vec3 diffTerm = texture(diffWall, vertexDataGeo.texCoords).rgb;
    vec3 normTerm = texture(normWall, vertexDataGeo.texCoords).rgb;
    vec3 specTerm = texture(specWall, vertexDataGeo.texCoords).rgb;

    normTerm = rgbToNormalizedXyz(normTerm);
    vec3 normalizedNormal = normalize(TBN * normTerm);
    normalizedToCamera = normalize(toCameraGeo);

    /* fast bugfixing */
    if (shaderType == 1) {
        color += vec4(vec3(dot(diffTerm.rgb, lum)), color.a);
    } else {
        color += vec4(diffTerm, 1.0f);
    }

    /* brdfWall for each pointLight */
    for (int i = 0; i < pointLights.length; i++) {
        normalizedToPointLight_current = normalize(toPointLightsGeo[i]);
        normalizedReflectedToPointLight_current = reflect(-normalizedToPointLight_current, normalizedNormal);
        normalizedHalfwayDirection_current = normalize(normalizedToPointLight_current + normalizedToCamera);

        cosAlpha_current = getCosAngle(normalizedNormal, normalizedToPointLight_current);
        cosBetaK_current = getCosAngleK(getCosAngle(normalizedReflectedToPointLight_current, normalizedToCamera));
        cosBetaK_Halfway_current = getCosAngleK(getCosAngle(normalizedHalfwayDirection_current, normalizedNormal));

        /* brdf with YELLOW spotLight */
        color += vec4(brdfWall(diffTerm, specTerm, cosAlpha_current, cosBetaK_Halfway_current, pointLights[i].color, toPointLightsGeo[i]), 0.0f);
    }
}