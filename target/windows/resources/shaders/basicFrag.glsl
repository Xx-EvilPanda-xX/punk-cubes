#version 330 core
in vec2 passTextureCoords;
in vec3 passColor;
in vec3 passNormal;
in vec3 fragPos;

out vec4 FragColor;

uniform int mode;
uniform sampler2D tex;
uniform vec3 lightColor;
uniform vec3 lightPos;
uniform vec3 viewPos;
//modes:
//0: Texture, with projection and view matrix
//1: Color, with projection and view matrix
//2: Texture, without projection and view matrix, no lighting
//3: Color, without projection and view matrix, no lighting

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
        vec4 finalResult = vec4(0.0, 0.0, 0.0, 0.0);

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
        vec3 result = (ambient + diffuse + specular) * vec3(color.rgb);
        finalResult += vec4(result, color.w);

        FragColor = finalResult;
    }
    else{
        FragColor = vec4(passColor, 1.0);
    }
}
