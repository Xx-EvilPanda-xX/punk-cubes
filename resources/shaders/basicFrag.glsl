#version 330 core
in vec2 passTextureCoords;
in vec3 passColor;
in vec3 passNormal;
in vec3 fragPos;


out vec4 FragColor;


uniform sampler2D tex;

uniform vec3 lightColor;
uniform vec3 lightPos;
uniform vec3 viewPos;

uniform int mode;

//modes:
//0: Texture, with projection and view matrix
//1: Color, with projection and view matrix
//2: Texture, without projection and view matrix, no lighting
//3: Color, without projection and view matrix, no lighting

vec3 calculateLighting(vec3 lightingColor, vec3 lightPosition, vec3 viewerPosition, vec3 fragNormal, vec3 fragPosition){
    //calculate ambient lighting
    float ambientStrength = 0.5;
    vec3 ambient = ambientStrength * lightingColor;

    //calculate diffuse lighting
    vec3 norm = normalize(fragNormal);
    vec3 lightDir = normalize(lightPosition - fragPosition);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightingColor;

    //calculate specular lighting
    float specularStrength = 0.8;
    vec3 viewDir = normalize(viewerPosition - fragPosition);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 64);
    vec3 specular = specularStrength * spec * lightingColor;

    return (ambient + diffuse + specular);
}

void main(){
    //find the initial fragment color in the texture or color buffer
    vec4 color;
    if (mode == 0 || mode == 2){
        color = texture(tex, passTextureCoords);
    }
    else{
        color = vec4(passColor, 1.0);
    }

    if (mode == 0 || mode == 1){
        //final result
        vec3 result = calculateLighting(lightColor, lightPos, viewPos, passNormal, fragPos) * vec3(color.rgb);

        vec3 environmentalLight1 = calculateLighting(vec3(0.5, 0.5, 0.5), vec3(100.0, 100.0, 100.0), viewPos, passNormal, fragPos);
        vec3 environmentalLight2 = calculateLighting(vec3(0.5, 0.5, 0.5), vec3(-100.0, -100.0, -100.0), viewPos, passNormal, fragPos);
        vec3 environmentalLight = environmentalLight1 + environmentalLight2;
        result *= environmentalLight;
        vec4 finalResult = vec4(result, color.w);

        FragColor = finalResult;
    }
    else{
        FragColor = color;
    }
}
