#version 150 core
//Line vert

uniform vec2 scale;
uniform vec2 offset;

in vec2 in_Position;

void main(void){
	gl_Position = vec4((in_Position+offset)*scale, 0, 1);
}