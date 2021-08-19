#version 330 core
#define NUM_POINT_LIGHTS 5

uniform sampler2D diffTex;
uniform sampler2D emitTex;
uniform sampler2D specTex;
uniform float shininess;
uniform vec3 emitColor;

uniform vec3 spotLightColor;

uniform vec3 spotLightDirection;
uniform float spotLightInnerCone;
uniform float spotLightOuterCone;

uniform vec3 spotLightAttenuation;

uniform int shaderType;

const float levels = 3.0;

//input from vertex shader
in struct VertexData {
    vec3 position;
    vec2 textureCoordinates;
    vec3 normal;
} vertexData;

in vec3 toSpotLight;

in struct PointLight {
    vec3 position;
    vec3 color;
    vec3 attenuation;
};
uniform PointLight pointLights[NUM_POINT_LIGHTS];
in vec3 toPointLights[NUM_POINT_LIGHTS];

in vec3 toCamera;

//fragment shader output
out vec4 color;

float attenuation(vec3 attenuations, vec3 toLight) {
    float distance = length(toLight);
    return 1.0f / (attenuations.x + attenuations.y * distance + attenuations.z * (pow(distance, 2)));
}

float getCosAngle(vec3 normalizedVector1, vec3 normalizedVector2) {
    return max(0.0f, dot(normalizedVector1, normalizedVector2));
}

float getCosAngleK(float cosAngle) {
    return pow(cosAngle, shininess);
}

//Phong Beleuchtungsfunktion
vec3 brdf(vec3 diffTerm, vec3 specTerm, float cosAlpha, float cosBetaK, vec3 lightColor, vec3 lightAttenuation, vec3 toLight) {
    return (diffTerm * cosAlpha + specTerm * cosBetaK) * lightColor * attenuation(lightAttenuation, toLight);
}

void main(){
    vec3 diffTerm = texture(diffTex, vertexData.textureCoordinates).rgb;
    vec3 specTerm = texture(specTex, vertexData.textureCoordinates).rgb;

    vec3 normalizedNormal = normalize(vertexData.normal);
    vec3 normalizedToCamera = normalize(toCamera);

    vec3 normalizedToSpotLight = normalize(toSpotLight);
    vec3 normalizedReflectedToSpotLight = reflect(-normalizedToSpotLight, normalizedNormal);
    vec3 normalizedSpotLightDirection = normalize(spotLightDirection);

    vec3 normalizedToPointLight_current, normalizedReflectedToPointLight_current, normalizedHalfwayDirection_current;
    float cosAlpha_current, cosBetaK_current, cosBetaK_Halfway_current;

    for (int i = 0; i < NUM_POINT_LIGHTS; i++) {
        normalizedToPointLight_current = normalize(toPointLights[i]);
        normalizedReflectedToPointLight_current = reflect(-normalizedToPointLight_current, normalizedNormal);
        normalizedHalfwayDirection_current = normalize(normalizedToPointLight_current + normalizedToCamera);

        cosAlpha_current = getCosAngle(normalizedNormal, normalizedToPointLight_current);
        cosBetaK_current = getCosAngleK(getCosAngle(normalizedReflectedToPointLight_current, normalizedToCamera));
        cosBetaK_Halfway_current = getCosAngleK(getCosAngle(normalizedHalfwayDirection_current, normalizedNormal));

        if(shaderType == 2){
            cosAlpha_current = floor(cosAlpha_current * levels) / levels;
            cosBetaK_current = floor(cosBetaK_current * levels) / levels;
        }

        // cosBetaK => Phong || cosBetaK_Halfway => Phong-Blinn
        color += vec4(brdf(diffTerm, specTerm, cosAlpha_current, cosBetaK_Halfway_current, pointLights[i].color, pointLights[i].attenuation, toPointLights[i]), 0.0f);
    }

    float cosTheta = getCosAngle(-normalizedToSpotLight, normalizedSpotLightDirection);
    float cosEpsilon = spotLightInnerCone - spotLightOuterCone;
    float intensity = clamp((cosTheta - spotLightOuterCone) / cosEpsilon, 0.0f, 1.0f);

    float cosAlpha_Spot = getCosAngle(normalizedNormal, normalizedToSpotLight);
    float cosBetaK_Spot = getCosAngleK(getCosAngle(normalizedReflectedToSpotLight, normalizedToCamera));

    color += vec4(brdf(diffTerm, specTerm, cosAlpha_Spot, cosBetaK_Spot, spotLightColor, spotLightAttenuation, toSpotLight), 0.0f) * intensity;

    color += vec4(texture(emitTex, vertexData.textureCoordinates).rgb * emitColor, 0.0f);

    vec3 lum = vec3(0.299f, 0.587f, 0.114f);

    if(shaderType == 1){
        color = vec4(vec3(dot(color.rgb, lum)), color.a);
    }
}