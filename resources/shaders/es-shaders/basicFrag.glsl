#version 300 es
in mediump vec2 passTextureCoords;
in mediump vec3 passColor;
in mediump vec3 passNormal;
in mediump vec3 fragPos;

out mediump vec4 FragColor;

uniform mediump sampler2D tex;
uniform mediump vec3 lightColor;
uniform mediump vec3 lightPos;
uniform mediump vec3 viewPos;
uniform bool hasColors;

void main(){
    //find the initial fragment color in the texture or color buffer
    mediump vec4 color;
    if (!hasColors){
        color = texture(tex, passTextureCoords);
    }
    else{
        color = vec4(passColor, 1.0);
    }

    //calculate ambient lighting
    mediump float ambientStrength = 0.5;
    mediump vec3 ambient  = ambientStrength * lightColor;

    //calculate diffuse lighting
    mediump vec3 norm = normalize(passNormal);
    mediump vec3 lightDir = normalize(lightPos - fragPos);
    mediump float diff = max(dot(norm, lightDir), 0.0);
    mediump vec3 diffuse = diff * lightColor;

    //calculate specular lighting
    mediump float specularStrength = 0.8;
    mediump vec3 viewDir = normalize(viewPos - fragPos);
    mediump vec3 reflectDir = reflect(-lightDir, norm);
    mediump float spec = pow(max(dot(viewDir, reflectDir), 0.0), 64.0);
    mediump vec3 specular = specularStrength * spec * lightColor;

    //final result
    mediump vec4 result = (vec4(ambient, 1.0) + vec4(diffuse, 1.0) + vec4(specular, 1.0)) * color;
    FragColor = result;
}
