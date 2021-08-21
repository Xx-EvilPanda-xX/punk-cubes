#version 330 core

in vec2 passTexCoords;

out vec4 FragColor;

uniform sampler2D tex;

void main(){
    FragColor = texture(tex, passTexCoords);
    if (FragColor.a < 0.1){
        discard;
    }
}