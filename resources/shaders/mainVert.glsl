#version 330 core
layout (location = 0) in vec3 vPos;
layout (location = 1) in vec2 vTexCoords;
layout (location = 2) in vec3 vNormal;
layout (location = 3) in mat4 model;

out vec2 passTextureCoords;
out vec3 passNormal;
out vec3 fragPos;

uniform mat4 projection;
uniform mat4 view;

uniform int mode;

//modes:
//0: Texture, with projection and view matrix
//1: Texture, without projection and view matrix, no lighting

void main(){
    if (mode == 1){
        gl_Position = model * vec4(vPos, 1.0);
        passNormal = vNormal;
        fragPos = vPos;
    } else {
        gl_Position = projection * view * model * vec4(vPos, 1.0);
        passNormal = mat3(transpose(inverse(model))) * vNormal;
        fragPos = vec3(model * vec4(vPos, 1.0));
    }

    passTextureCoords = vTexCoords;
}
