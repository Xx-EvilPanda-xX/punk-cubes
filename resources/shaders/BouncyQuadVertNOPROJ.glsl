#version 330 core
layout (location = 0) in vec3 vPos;
layout (location = 1) in vec3 vColor;

out vec3 passColor;

uniform mat4 model;

void main(){
    gl_Position = model * vec4(vPos, 1.0);
    passColor = vColor;
}
