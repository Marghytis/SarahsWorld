#version 150 core

in vec2 pass_TexCoords;
in vec4 pass_Alphas;
in vec4 pass_indices;

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
	vec4 color =  texture(getSampler(pass_indices[0]), pass_TexCoords)*pass_Alphas[0]
				+ texture(getSampler(pass_indices[1]), pass_TexCoords)*pass_Alphas[1]
				+ texture(getSampler(pass_indices[2]), pass_TexCoords)*pass_Alphas[2]
				+ texture(getSampler(pass_indices[3]), pass_TexCoords)*pass_Alphas[3]
	vec4 texColor = texture(texture_diffuse, pass_TexCoords);
		out_Color = vec4(texColor.rgb, texColor.a*pass_Alpha.y*pass_Alpha.x);
	//}
}
void getSampler(in float index, out sampler2D sampler)
{
  if(index == 0){
  	sampler = texture0;
  } else if(index == 1){
  	sampler = texture1;
  } else if(index == 2){
  	sampler = texture2;
  } else if(index == 3){
  	sampler = texture3;
  } else if(index == 4){
  	sampler = texture4;
  } else if(index == 5){
  	sampler = texture5;
  } else if(index == 6){
  	sampler = texture6;
  } else if(index == 7){
  	sampler = texture7;
  } else if(index == 8){
  	sampler = texture8;
  } else if(index == 9){
  	sampler = texture9;
  } else if(index == 10){
  	sampler = texture10;
  } else if(index == 11){
  	sampler = texture11;
  } else if(index == 12){
  	sampler = texture12;
  } else if(index == 13){
  	sampler = texture13;
  } else if(index == 14){
  	sampler = texture14;
  } else if(index == 15){
  	sampler = texture15;
  } else if(index == 16){
  	sampler = texture16;
  } else if(index == 17){
  	sampler = texture17;
  } else if(index == 18){
  	sampler = texture18;
  } else if(index == 19){
  	sampler = texture19;
  }
}

uniform sampler2D texture0;
uniform sampler2D texture1;
uniform sampler2D texture2;
uniform sampler2D texture3;
uniform sampler2D texture4;
uniform sampler2D texture5;
uniform sampler2D texture6;
uniform sampler2D texture7;
uniform sampler2D texture8;
uniform sampler2D texture9;
uniform sampler2D texture10;
uniform sampler2D texture11;
uniform sampler2D texture12;
uniform sampler2D texture13;
uniform sampler2D texture14;
uniform sampler2D texture15;
uniform sampler2D texture16;
uniform sampler2D texture17;
uniform sampler2D texture18;
uniform sampler2D texture19;

