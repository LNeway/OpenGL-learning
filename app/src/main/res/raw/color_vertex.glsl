attribute vec3 aPos;
attribute vec3 v_color;
varying vec3 outColor;

void main() {
    gl_Position = vec4(aPos, 1);
    outColor = v_color;
}