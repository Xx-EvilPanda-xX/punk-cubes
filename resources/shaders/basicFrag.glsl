#version 330 core
in vec2 passTextureCoords;
in vec3 passColor;
in vec3 passNormal;
in vec3 fragPos;
flat in int passMode;
flat in int passColorMode;

out vec4 FragColor;

uniform int numLights;
uniform sampler2D tex;
uniform float red;
uniform float green;
uniform float blue;
uniform vec3 lightColor;
uniform vec3 lightPos;
uniform vec3 viewPos;
//modes:
//0: Texture, with projection and view matrix
//1: Color, with projection and view matrix
//2: Texture, without projection and view matrix, no lighting
//3: Color, without projection and view matrix, no lighting
//4: Texture, with projection and view matrix, no lighting
//5: Color, with projection and view matrix, no lighting

void main(){
    //find the initial fragment color in the texture or color buffer
    vec4 color;
    if (passMode == 0 || passMode == 2){
        color = texture(tex, passTextureCoords);
    }
    else{
        color = vec4(passColor, 1.0);
    }

    if (passMode == 0 || passMode == 1){
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
    else {
        if (passColorMode == 0){
            FragColor = vec4(red, passColor.yz, 1.0);
        }
        if (passColorMode == 1){
            FragColor = vec4(passColor.x, green, passColor.z, 1.0);
        }
        if (passColorMode == 2){
            FragColor = vec4(passColor.xy, blue, 1.0);
        }
        else{
            FragColor = vec4(passColor, 1.0);
        }
    }
}
