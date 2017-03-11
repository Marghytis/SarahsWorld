#version 150 core

uniform vec2 windowSize;
uniform vec4 colorsTop[10];
uniform vec4 colorsBottom[10];

out vec4 out_Color;

void main(void){

	int index = int(gl_FragCoord.x/100.0);
	float yScale = gl_FragCoord.y/windowSize.y;
	vec4 colorLeft = ((1-yScale)*colorsBottom[index]) + (yScale*colorsTop[index]);
	vec4 colorRight = ((1-yScale)*colorsBottom[index+1]) + (yScale*colorsTop[index+1]);
	
	float xScale = (gl_FragCoord.x - (gl_FragCoord.x/100.0))/100.0;

	out_Color = vec4(((1-xScale)*colorLeft) + (xScale*colorRight));
}
