package com.devtom.opengllearning;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by tomliu on 2017/8/27.
 */

public class MatrixRender implements GLSurfaceView.Renderer {

    private Context context;

    private int mProgram;
    private final float vertext [] = {
            -0.5f, 0.5f, 0,
            1, 1, 0,
            1, -1, 0,
    };

    private final short position [] = {0, 1, 2};


    private FloatBuffer verextBuffer = null;
    private ShortBuffer shortBuffer = null;

    private int v_position;
    private int matrixHandle;

    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];


    public MatrixRender(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        verextBuffer = ByteBuffer.allocateDirect(vertext.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(vertext);
        verextBuffer.position(0);

        shortBuffer = ByteBuffer.allocateDirect(position.length * 2).order(ByteOrder.nativeOrder())
                .asShortBuffer().put(position);
        shortBuffer.position(0);

        mProgram = GLES20.glCreateProgram();
        int vertexShader = Util.createShade(GLES20.GL_VERTEX_SHADER, Util.readTextResourceFromRaw(context, R.raw.matrix_vertex));
        int fragmentShader = Util.createShade(GLES20.GL_FRAGMENT_SHADER, Util.readTextResourceFromRaw(context, R.raw.matrix_fragment));
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        GLES20.glUseProgram(mProgram);


        v_position = GLES20.glGetAttribLocation(mProgram, "aPos");
        GLES20.glEnableVertexAttribArray(v_position);
        GLES20.glVertexAttribPointer(v_position, 3, GLES20.GL_FLOAT, false,
                12, verextBuffer);


        matrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0, width, height);
        float ratio=(float)width/height;
        /**
         * 设置透视投影
         *
         * -- float[] m 参数 : 生成矩阵元素的 float[] 数组;
         -- int mOffset 参数 : 矩阵数组的起始偏移量;
         -- float left, float right, float bottom, float top 参数 : 近平面的 左, 右, 下, 上 的值;
         -- float near 参数 : 近平面 与 视点之间的距离;
         -- float far 参数 : 远平面 与 视点之间的距离
         *
         */

        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 1, 20);
        /**
         * 设置相机的位置，这里实际上是设置我们看屏幕的位置，我们看屏幕的位置不同，最终看到
         * 的图形也会有很大的不同
         *
         * 这里的这里 @param eyeZ eye point Z，实际上是对应了设置透视投影 的 (near, far), 在这个区间
         * 内可以正常的看到图形，并且产生近大远小的效果，但是如果超出了这个范围就不会显示了。
         *
         *
         *
         */
        Matrix.setLookAtM(mViewMatrix, 0, 0.25f, 0f, 4f, 0.25f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);

        Matrix.translateM(mMVPMatrix, 0, 0, 1, 10);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mMVPMatrix,0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
