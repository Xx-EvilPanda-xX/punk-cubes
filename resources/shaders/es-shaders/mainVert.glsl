#version 300 es
layout (location = 0) in highp vec3 vPos;
layout (location = 1) in highp vec2 vTexCoords;
layout (location = 2) in highp vec3 vNormal;
layout (location = 3) in highp mat4 model;

out highp vec2 passTextureCoords;
out highp vec3 passNormal;
out highp vec3 fragPos;

uniform highp mat4 projection;
uniform highp mat4 view;

uniform highp int mode;

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

