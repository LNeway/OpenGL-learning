
precision mediump float;
varying vec2 outTexCoord;
uniform sampler2D u_samplerTexture;

void main()
{
 gl_FragColor = texture2D(u_samplerTexture, outTexCoord);
}