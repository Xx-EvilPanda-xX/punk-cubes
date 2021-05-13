#version 300 es
layout (location = 0) in vec3 vPos;
layout (location = 1) in vec3 vColor;

out mediump vec3 passColor;

uniform mat4 model;
uniform mat4 projection;
uniform mat4 view;

void main(){
    gl_Position = projection * view * model * vec4(vPos, 1.0);
    passColor = vColor;
}