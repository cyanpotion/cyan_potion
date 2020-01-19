#version 120

uniform sampler2D sampler;
uniform vec4 colorScale;

varying vec2 tex_coords;

void main() {
	gl_FragColor = texture2D(sampler, tex_coords) * colorScale;
}