#version 150 core

uniform sampler2D texture_diffuse;

in vec2 texCoords;
in vec4 color;

out vec4 out_Color;

void main(void){

	out_Color = texture(texture_diffuse, texCoords)*color;
}
