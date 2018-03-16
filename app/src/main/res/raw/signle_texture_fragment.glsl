
precision mediump float;
varying vec2 outTexCoord;
uniform sampler2D u_samplerTexture;

void main() {
    vec2 location = (1 - outTexCoord.x, outTexCoord.y);
    gl_FragColor = texture2D(u_samplerTexture, location);
}