#version 150 core

in vec2 in_position;
in float in_rotation;
in vec2 in_texCoords;
in float in_mirror;
in vec4 in_color;
in float in_z;
in float in_size;
in vec4 in_box;
in vec2 in_texWH;

out Vertex {
	float pass_rotation;
	vec2 pass_texXY;
	float pass_mirror;
	vec4 pass_color;
	float pass_size;
	vec4 pass_box;
	vec2 pass_texWH;
} vertex;

void main(void){
	gl_Position = vec4(in_position, in_z, 1);
	
	vertex.pass_rotation = in_rotation;
	vertex.pass_texXY = in_texCoords;
	vertex.pass_mirror = in_mirror;
	vertex.pass_color = in_color;
	vertex.pass_size = in_size;
	vertex.pass_box = in_box;
	vertex.pass_texWH = in_texWH;
}