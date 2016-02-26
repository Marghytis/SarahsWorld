uniform sampler2D texSampler;
uniform vec4 color;

void main(void)
{
	vec4 tex = texture2D(texSampler, gl_TexCoord[0]);
	if(max(tex.r, max(tex.g, tex.b)) < 0.6 && tex.a != 0){
		gl_FragColor = color;
	} else {
		gl_FragColor = vec4(0, 0, 0, 0);
	}
}