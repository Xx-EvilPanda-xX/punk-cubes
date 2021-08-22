#version 330 core
layout (location = 0) in vec2 vPos;
layout (location = 1) in vec2 vTexCoords;

out vec2 passTexCoords;

uniform mat4 model;

void main(){
    gl_Position = model * vec4(vPos, 1.0, 1.0);
    passTexCoords = vTexCoords;
}