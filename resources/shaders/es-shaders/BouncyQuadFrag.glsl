#version 300 es
in mediump vec3 passColor;

out mediump vec4 FragColor;

uniform mediump float red;

void main(){
    FragColor = vec4(red, passColor.yz, 1.0);
}
