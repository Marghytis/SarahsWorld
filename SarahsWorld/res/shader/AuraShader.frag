uniform sampler2D texSampler;

void main(void)
{
	gl_FragColor = vec4(gl_Color.rgb, texture2D(texSampler, gl_TexCoord[0].xy).a);
}