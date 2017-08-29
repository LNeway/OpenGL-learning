uniform mat4 u_MVPMatrix;

attribute vec4 a_Position;          // Per-vertex position information we will pass in.
attribute vec4 a_Color;             // Per-vertex color information we will pass in.
varying vec4 v_Color;               // This will be passed
void main()                         // The entry point for our vertex shader.
{
   v_Color = a_Color;               // Pass the color through to the fragment shader.
   gl_Position = u_MVPMatrix * a_Position;     // Multiply the vertex by the matrix to get the final point in
}                                // normalized screen coordinates.