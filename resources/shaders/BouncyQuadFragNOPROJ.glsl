#version 330 core
in vec3 passColor;

out vec4 FragColor;

uniform float red;

void main(){
    FragColor = vec4(red, passColor.yz, 1.0);
}
