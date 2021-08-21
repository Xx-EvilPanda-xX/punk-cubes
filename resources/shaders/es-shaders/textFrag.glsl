#version 300 es

in highp vec2 passTexCoords;

out highp vec4 FragColor;

uniform highp sampler2D tex;

void main(){
    FragColor = texture(tex, passTexCoords);
    if (FragColor.a < 0.1){
        discard;
    }
}