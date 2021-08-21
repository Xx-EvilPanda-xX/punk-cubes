#version 300 es
layout (location = 0) in highp vec2 vPos;
layout (location = 1) in highp vec2 vTexCoords;

out highp vec2 passTexCoords;

uniform highp mat4 model;

void main(){
    gl_Position = model * vec4(vPos, -1.0, 1.0);
    passTexCoords = vTexCoords;
}