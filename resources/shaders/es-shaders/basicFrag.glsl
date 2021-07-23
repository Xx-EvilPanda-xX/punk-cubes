#version 300 es
in highp vec2 passTextureCoords;
in highp vec3 passColor;
in highp vec3 passNormal;
in highp vec3 fragPos;

out highp vec4 FragColor;

uniform highp int mode;
uniform highp sampler2D tex;
uniform highp vec3 lightColor;
uniform highp vec3 lightPos;
uniform highp vec3 viewPos;
//modes:
//0: Texture, with projection and view matrix
//1: Color, with projection and view matrix
//2: Texture, without projection and view matrix, no lighting
//3: Color, without projection and view matrix, no lighting

void main(){
    //find the initial fragment color in the texture or color buffer
    highp vec4 color;
    if (mode == 0 || mode == 2){
        color = texture(tex, passTextureCoords);
    }
    else{
        color = vec4(passColor, 1.0);
    }

    if (mode == 0 || mode == 1){
        highp vec4 finalResult = vec4(0.0, 0.0, 0.0, 0.0);

        //calculate ambient lighting
        highp float ambientStrength = 0.5;
        highp vec3 ambient  = ambientStrength * lightColor;

        //calculate diffuse lighting
        highp vec3 norm = normalize(passNormal);
        highp vec3 lightDir = normalize(lightPos - fragPos);
        highp float diff = max(dot(norm, lightDir), 0.0);
        highp vec3 diffuse = diff * lightColor;

        //calculate specular lighting
        highp float specularStrength = 0.8;
        highp vec3 viewDir = normalize(viewPos - fragPos);
        highp vec3 reflectDir = reflect(-lightDir, norm);
        highp float spec = pow(max(dot(viewDir, reflectDir), 0.0), 64.0);
        highp vec3 specular = specularStrength * spec * lightColor;

        //final result
        highp vec3 result = (ambient + diffuse + specular) * vec3(color.rgb);
        finalResult += vec4(result, color.w);

        FragColor = finalResult;
    }
    else{
        FragColor = color;
    }
}
