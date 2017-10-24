#version 150 core

uniform sampler2D texture_diffuse;

in vec2 texCoords;
in vec4 color;

out vec4 out_Color;

void main(void){

	vec4 tex = texture(texture_diffuse, texCoords);
	if(tex.a != 0){
		ivec2 size = textureSize(texture_diffuse, 0);
		ivec2 pos = ivec2(int(texCoords.x*size.x), int(texCoords.y*size.y));
	
		bool hmpf = false;
	   	for (int i=0; i<3; i++){
	        for (int j=0; j<3; j++){
	        	if(texelFetch(texture_diffuse, pos + ivec2(i-1, j-1), 0).a <= 0.3){
	        		hmpf = true;
	        	}
	        }
	    } 
	//	if(max(tex.r, max(tex.g, tex.b)) < 0.6 && tex.a != 0){
		if(hmpf){
			out_Color = vec4(0.1, 0.1, 0.1, 1);
		} else {
			out_Color = vec4(0, 0, 0, 0);
		}
	} else {
		out_Color = vec4(0, 0, 0, 0);
	}
}
