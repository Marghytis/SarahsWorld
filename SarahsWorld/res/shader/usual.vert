#version 150 core
//usual

uniform vec2 scale;
uniform vec3 offset;

in vec2 in_position;
in vec2 in_texCoords;

out vec2 pass_texCoords;

void main(void){
	gl_Position = vec4((in_position + offset.xy)*scale, offset.z, 1);
	
	pass_texCoords = in_texCoords;
}