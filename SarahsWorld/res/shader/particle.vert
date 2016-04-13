#version 150 core

uniform vec2 scale;
uniform vec2 offset;

//per vertex
in vec2 pos1;
in vec2 in_texCoords;

//per particle
in float draw;
in vec2 pos2;
in vec2 rot;//vec2(cos(phi), sin(phi))
in float size;
in vec4 in_color;

out vec2 pass_texCoords;
out vec4 pass_color;

void main(void){
	if(draw == 1){
		gl_Position = vec4(
		scale.x*(size*((rot.x*pos1.x) - (rot.y*pos1.y)) + pos2.x + offset.x),
		scale.y*(size*((rot.y*pos1.x) + (rot.x*pos1.y)) + pos2.y + offset.y), 0, 1);
		pass_color = in_color;
	} else {
		gl_Position = vec4(123.456789, 0, 0, 1);
		pass_color = vec4(0, 1, 1, 1); 
	}
	//gl_Position = vec4(scale*(pos1*size+pos2+offset), 0, 1);
	//pass_color = in_color*2;//TODO remove the 2
	pass_texCoords = in_texCoords;
}