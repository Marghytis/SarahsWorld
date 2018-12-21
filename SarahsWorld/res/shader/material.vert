#version 150 core

uniform vec4 transform;
uniform int matSlot;
uniform int points;

in vec2 in_Position;
in vec2 in_TextureCoords;
in vec4 in_Alphas;
in float in_TransAlpha;

out vec2 pass_TexCoords;
out vec2 pass_Alpha;

void main(void){
	gl_Position = vec4((in_Position + transform.xy)*transform.zw, 0, 1);
	if(points == 1){
		gl_PointSize = 10;
		pass_Alpha = vec2(in_Alphas[matSlot], 0.5*in_TransAlpha);
	} else {
		pass_Alpha = vec2(in_Alphas[matSlot], in_TransAlpha);
	}
	
	pass_TexCoords = in_TextureCoords;
}
