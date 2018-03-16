attribute vec3 aPos;
attribute vec2 v_texCoord;
varying vec2 outTexCoord;

void main()
{
    gl_Position =  vec4(aPos, 1.0);
    outTexCoord = v_texCoord;
}