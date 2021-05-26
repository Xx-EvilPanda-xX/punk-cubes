#version 330 core
layout (location = 0) in vec3 vPos;
layout (location = 1) in vec2 vTexCoords;
layout (location = 2) in vec3 vColor;
layout (location = 3) in vec3 vNormal;

out vec2 passTextureCoords;
out vec3 passNormal;
out vec3 fragPos;
out vec3 passColor;

uniform mat4 model;
uniform mat4 projection;
uniform mat4 view;
uniform int mode;
//modes:
//0: Texture, with projection and view matrix
//1: Color, with projection and view matrix
//2: Texture, without projection and view matrix, no lighting
//3: Color, without projection and view matrix, no lighting

void main(){
    if (mode == 2 || mode == 3){
        gl_Position = model * vec4(vPos, 1.0);
    } else {
        gl_Position =  projection * view * model * vec4(vPos, 1.0);
    }
    passTextureCoords = vTexCoords;
    passNormal = mat3(transpose(inverse(model))) * vNormal;
    fragPos = vec3(model * vec4(vPos, 1.0));
    passColor = vColor;
}
