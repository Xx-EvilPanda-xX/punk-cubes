#version 300 es
layout (location = 0) in vec3 vPos;
layout (location = 1) in vec2 vTexCoords;
layout (location = 2) in vec3 vColor;
layout (location = 3) in vec3 vNormal;

out mediump vec2 passTextureCoords;
out mediump vec3 passNormal;
out mediump vec3 fragPos;
out mediump vec3 passColor;

uniform mat4 model;
uniform mat4 projection;
uniform mat4 view;

void main(){
    gl_Position =  projection * view * model * vec4(vPos, 1.0);
    passTextureCoords = vTexCoords;
    passNormal = mat3(transpose(inverse(model))) * vNormal;
    fragPos = vec3(model * vec4(vPos, 1.0));
    passColor = vColor;
}
