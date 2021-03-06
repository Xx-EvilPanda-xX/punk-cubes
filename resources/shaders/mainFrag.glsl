#version 330 core
in vec2 passTextureCoords;
in vec3 passNormal;
in vec3 fragPos;


out vec4 FragColor;


uniform sampler2D tex;

struct Material {
    vec3 Ka;
    vec3 Kd;
    vec3 Ks;
    float spec;
};

uniform Material material;

uniform vec3 lightColor;
uniform vec3 lightPos;
uniform vec3 viewPos;

uniform int mode;
uniform bool useMaterialDiffuse;

//modes:
//0: Texture, with projection and view matrix
//1: Texture, without projection and view matrix, no lighting

vec3 calculateLighting(vec3 lightingColor, vec3 lightPosition, vec3 viewerPosition, vec3 fragNormal, vec3 fragPosition){
    //calculate diffuse lighting
    vec3 norm = normalize(fragNormal);
    vec3 lightDir = normalize(lightPosition - fragPosition);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightingColor;

    //calculate ambient lighting
    float ambientStrength = 0.5;
    vec3 ambient = ambientStrength * lightingColor * material.Ka;

    //calculate specular lighting
    float specularStrength = 0.8;
    vec3 viewDir = normalize(viewerPosition - fragPosition);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.spec);
    vec3 specular = specularStrength * spec * lightingColor * material.Ks;

    return (diffuse + ambient + specular);
}

void main(){
    //find the initial fragment color in the texture or color buffer
    vec4 color;

    if (useMaterialDiffuse){
        color = vec4(material.Kd, 1.0);
    }
    else{
        color = texture(tex, passTextureCoords);
    }

    if (mode == 0){
        //final result
        vec3 result = calculateLighting(lightColor, lightPos, viewPos, passNormal, fragPos) * vec3(color.rgb);

        vec3 environmentalLight1 = calculateLighting(vec3(0.65, 0.65, 0.65), vec3(100.0, 100.0, 100.0), viewPos, passNormal, fragPos);
        vec3 environmentalLight2 = calculateLighting(vec3(0.65, 0.65, 0.65), vec3(-100.0, -100.0, -100.0), viewPos, passNormal, fragPos);
        result *= environmentalLight1 + environmentalLight2;
        vec4 finalResult = vec4(result, color.w);

        FragColor = finalResult;
    }
    else{
        FragColor = color;
    }
}
