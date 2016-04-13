#version 150 core

uniform vec4 transform;

in vec2 in_Position;
in vec4 in_Color;

out vec4 pass_Color;

void main(void){
	gl_Position = vec4((in_Position + transform.xy)*transform.zw, 0, 1);
	
	pass_Color = in_Color;
}