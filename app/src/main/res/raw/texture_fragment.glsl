
precision mediump float;
varying vec2 outTexCoord;
uniform sampler2D u_samplerTexture;
uniform sampler2D watermark;

void main()
{
 gl_FragColor = mix(texture2D(u_samplerTexture, outTexCoord) * vec4(0, 1, 0, 1), texture2D(watermark, outTexCoord), 0.2);
}