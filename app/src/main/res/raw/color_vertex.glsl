attribute vec3 aPos;
attribute vec3 v_color;
varying vec3 outColor;  // varying 修饰参数会从顶点着色器中传递到片段着色器中

void main() {
    gl_Position = vec4(aPos, 1);
    outColor = v_color;
}