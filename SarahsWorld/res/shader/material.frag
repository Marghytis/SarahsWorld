#version 150 core

uniform sampler2D texture_diffuse;

in vec2 pass_TexCoords;
in vec2 pass_Alpha;

out vec4 out_Color;

void main(void){

	//out_Color = vec4(1, 0, 0, 1);
	//if(alpha){
	//	out_Color = vec4(texture(texture_diffuse, pass_TexCoords).rgb*pass_Alpha.x, pass_Alpha.y);
	//} else {
	//	//cannot put the alpha in the alpha channel, background would bleed through.
	//	out_Color = vec4(texture(texture_diffuse, pass_TexCoords).rgb*pass_Alpha, 1);
	//}
	//if(transition){
	//	out_Color = vec4(texture(texture_diffuse, pass_TexCoords).rgb, pass_Alpha.y);
	//} else {
	vec4 texColor = texture(texture_diffuse, pass_TexCoords);
		out_Color = vec4(texColor.rgb, texColor.a*pass_Alpha.y*pass_Alpha.x);
	//}
}
