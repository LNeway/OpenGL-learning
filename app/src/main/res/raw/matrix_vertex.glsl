attribute vec3 aPos;
uniform mat4 vMatrix;

void main()
{
    gl_Position = vMatrix * vec4(aPos, 1.0);

}