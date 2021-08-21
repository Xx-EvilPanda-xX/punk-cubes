#version 300 es
in highp vec2 passTextureCoords;
in highp vec3 passNormal;
in highp vec3 fragPos;


out highp vec4 FragColor;


uniform highp sampler2D tex;

struct Material {
    highp vec3 Ka;
    highp vec3 Kd;
    highp vec3 Ks;
    highp float spec;
};

uniform Material material;

uniform highp vec3 lightColor;
uniform highp vec3 lightPos;
uniform highp vec3 viewPos;

uniform highp int mode;
uniform bool useMaterialDiffuse;

//modes:
//0: Texture, with projection and view matrix
//1: Texture, without projection and view matrix, no lighting

highp vec3 calculateLighting(vec3 lightingColor, vec3 lightPosition, vec3 viewerPosition, vec3 fragNormal, vec3 fragPosition){
    //calculate diffuse lighting
    highp vec3 norm = normalize(fragNormal);
    highp vec3 lightDir = normalize(lightPosition - fragPosition);
    highp float diff = max(dot(norm, lightDir), 0.0);
    highp vec3 diffuse = diff * lightingColor;

    //calculate ambient lighting
    highp float ambientStrength = 0.5;
    highp vec3 ambient = ambientStrength * lightingColor * material.Ka;

    //calculate specular lighting
    highp float specularStrength = 0.8;
    highp vec3 viewDir = normalize(viewerPosition - fragPosition);
    highp vec3 reflectDir = reflect(-lightDir, norm);
    highp float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.spec);
    highp vec3 specular = specularStrength * spec * lightingColor * material.Ks;

    return (diffuse + ambient + specular);
}

void main(){
    //find the initial fragment color in the texture or color buffer
    highp vec4 color;

    if (useMaterialDiffuse){
        color = vec4(material.Kd, 1.0);
    }
    else{
        color = texture(tex, passTextureCoords);
    }

    if (mode == 0){
        //final result
        highp vec3 result = calculateLighting(lightColor, lightPos, viewPos, passNormal, fragPos) * vec3(color.rgb);

        highp vec3 environmentalLight1 = calculateLighting(vec3(0.65, 0.65, 0.65), vec3(100.0, 100.0, 100.0), viewPos, passNormal, fragPos);
        highp vec3 environmentalLight2 = calculateLighting(vec3(0.65, 0.65, 0.65), vec3(-100.0, -100.0, -100.0), viewPos, passNormal, fragPos);
        result *= environmentalLight1 + environmentalLight2;
        highp vec4 finalResult = vec4(result, color.w);

        FragColor = finalResult;
    }
    else{
        FragColor = color;
    }
}
