#version 330 core

in vec3 FragPos;
in vec3 Normal;
in vec2 TexCoord;

uniform vec3 diffuseColor;
uniform vec3 specularColor;
uniform vec3 ambientColor;
uniform float shininess;

uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;
uniform sampler2D specularTexture;

uniform bool hasDiffuseTexture;
uniform bool hasNormalTexture;
uniform bool hasSpecularTexture;

uniform vec3 cameraPosition;

out vec4 FragColor;

void main() {
    // Base color
    vec3 color = diffuseColor;
    if (hasDiffuseTexture) {
        color = texture(diffuseTexture, TexCoord).rgb;
    }
    
    // Normal
    vec3 norm = normalize(Normal);
    if (hasNormalTexture) {
        // TODO: Implement normal mapping
        norm = normalize(Normal);
    }
    
    // Simple directional light
    vec3 lightDir = normalize(vec3(1.0, 1.0, 1.0));
    vec3 lightColor = vec3(1.0, 1.0, 1.0);
    
    // Ambient
    vec3 ambient = ambientColor * color;
    
    // Diffuse
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = lightColor * diff * color;
    
    // Specular
    vec3 viewDir = normalize(cameraPosition - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
    vec3 specular = lightColor * spec * specularColor;
    if (hasSpecularTexture) {
        specular *= texture(specularTexture, TexCoord).rgb;
    }
    
    // Combine
    vec3 result = ambient + diffuse + specular;
    FragColor = vec4(result, 1.0);
}
