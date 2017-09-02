package com.devtom.opengllearning;

/**
 * Created by tomliu on 2017/8/28.
 */


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ThreeDimensionRender implements GLSurfaceView.Renderer {
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer mCubePositions;
    private final FloatBuffer mCubeColors;
    private float[] mMVPMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private int mMVPMatrixHandle;
    private int mPositionHandle;
    private int mColorHandle;
    private int mProgramHandle;
    private final int POSITION_DATA_SIZE = 3;
    private final int COLOR_DATA_SIZE = 4;

    private Context context = null;


    // Position the eye behind the origin.
     float eyeX = 2f;
     float eyeY = 2f;
     float eyeZ = 2f;


    // Set our up vector. This is where our head would be pointing were we holding the camera.
     float upX = 1f;
     float upY = 1f;
     float upZ = 1f;



    public ThreeDimensionRender(Context context) {
        this.context = context;

        final float cubePosition[] =
                {
                        // Front face
                        -0.5f, 0.5f, 0.5f,
                        -0.5f, -0.5f, 0.5f,
                        0.5f, 0.5f, 0.5f,
                        -0.5f, -0.5f, 0.5f,
                        0.5f, -0.5f, 0.5f,
                        0.5f, 0.5f, 0.5f,

                        // Right face
                        0.5f, 0.5f, 0.5f,
                        0.5f, -0.5f, 0.5f,
                        0.5f, 0.5f, -0.5f,
                        0.5f, -0.5f, 0.5f,
                        0.5f, -0.5f, -0.5f,
                        0.5f, 0.5f, -0.5f,

                        // Back face
                        0.5f, 0.5f, -0.5f,
                        0.5f, -0.5f, -0.5f,
                        -0.5f, 0.5f, -0.5f,
                        0.5f, -0.5f, -0.5f,
                        -0.5f, -0.5f, -0.5f,
                        -0.5f, 0.5f, -0.5f,

                        // Left face
                        -0.5f, 0.5f, -0.5f,
                        -0.5f, -0.5f, -0.5f,
                        -0.5f, 0.5f, 0.5f,
                        -0.5f, -0.5f, -0.5f,
                        -0.5f, -0.5f, 0.5f,
                        -0.5f, 0.5f, 0.5f,

                        // Top face
                        -0.5f, 0.5f, -0.5f,
                        -0.5f, 0.5f, 0.5f,
                        0.5f, 0.5f, -0.5f,
                        -0.5f, 0.5f, 0.5f,
                        0.5f, 0.5f, 0.5f,
                        0.5f, 0.5f, -0.5f,

                        // Bottom face
                        0.5f, -0.5f, -0.5f,
                        0.5f, -0.5f, 0.5f,
                        -0.5f, -0.5f, -0.5f,
                        0.5f, -0.5f, 0.5f,
                        -0.5f, -0.5f, 0.5f,
                        -0.5f, -0.5f, -0.5f,
                };

        final float[] cubeColor =
                {
                        // Front face (red)
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,

                        // Right face (green)
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,

                        // Back face (blue)
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,

                        // Left face (yellow)
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 1.0f,

                        // Top face (cyan)
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,

                        // Bottom face (magenta)
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f
                };

        mCubePositions = ByteBuffer.allocateDirect(cubePosition.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubePositions.put(cubePosition).position(0);
        mCubeColors = ByteBuffer.allocateDirect(cubeColor.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeColors.put(cubeColor).position(0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // TODO Auto-generated method stub
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        GLES20.glUseProgram(mProgramHandle);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");


        // We are looking toward the distance
        final float lookX = 0f;
        final float lookY = 0f;
        final float lookZ = 0f;


        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        //Log.e("TAG", " eyeX, eyeY, eyeZ is"+  eyeX + "  "+ eyeY + " "+ eyeZ);

        Matrix.setIdentityM(mModelMatrix, 0);
        //Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -5.0f);
        //Matrix.rotateM(mModelMatrix, 0, angleInDegrees, angleInDegrees, 1.0f, 0.0f);
        drawCube(mCubePositions, mCubeColors);
    }

    private void drawCube(final FloatBuffer cubePositionsBuffer, final FloatBuffer cubeColorsBuffer) {
        cubePositionsBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, 0, cubePositionsBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        cubeColorsBuffer.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, 0, cubeColorsBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO Auto-generated method stub
        GLES20.glViewport(0, 0, width, height);

        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1f;
        final float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, bottom, top, near, far);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // TODO Auto-generated method stub
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);


        int vertex = Util.createShade(GLES20.GL_VERTEX_SHADER, Util.readTextResourceFromRaw(context,
                R.raw.three_d_vertex));

        int fragment = Util.createShade(GLES20.GL_FRAGMENT_SHADER, Util.readTextResourceFromRaw(context,
                R.raw.three_d_fragment));


        mProgramHandle = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgramHandle, vertex);
        GLES20.glAttachShader(mProgramHandle, fragment);

        GLES20.glBindAttribLocation(mProgramHandle, 0, "a_Position");
        GLES20.glBindAttribLocation(mProgramHandle, 1, "a_Color");

        GLES20.glLinkProgram(mProgramHandle);


    }

}
