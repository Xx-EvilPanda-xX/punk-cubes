#version 330 core
in vec2 passTextureCoords;
in vec3 passNormal;
in vec3 fragPos;

out vec4 FragColor;

uniform sampler2D tex;
uniform vec3 lightColor;
uniform vec3 lightPos;
uniform vec3 viewPos;

void main(){
    //find the initial fragment color in the texture
    vec4 texColor = texture(tex, passTextureCoords);

    //calculate ambient lighting
    float ambientStrength = 0.5;
    vec3 ambient  = ambientStrength * lightColor;

    //calculate diffuse lighting
    vec3 norm = normalize(passNormal);
    vec3 lightDir = normalize(lightPos - fragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    //calculate specular lighting
    float specularStrength = 0.8;
    vec3 viewDir = normalize(viewPos - fragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 64);
    vec3 specular = specularStrength * spec * lightColor;

    //final result
    vec4 result = (vec4(ambient, 1.0) + vec4(diffuse, 1.0) + vec4(specular, 1.0)) * texColor;
    FragColor = result;
}
