package com.devtom.opengllearning;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by tomliu on 2017/8/23.
 */

public class ColorfulRender implements GLSurfaceView.Renderer {


    private Context context;

    private int mProgram;
    private final float vertext [] = {
            0.5f, -0.5f, 0.0f,  1.0f, 0.0f, 0.0f,   // 右下
            -0.5f, -0.5f, 0.0f,  0.0f, 1.0f, 0.0f,   // 左下
            0.0f,  0.5f, 0.0f,  0.0f, 0.0f, 1.0f    // 顶部
    };

    private final float color[] = {
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f
    };


    private FloatBuffer verextBuffer = null;
    private FloatBuffer colorBuffer = null;

    private int v_position;
    private int color_position;


    private int dataBuffer;

    public ColorfulRender(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        verextBuffer = ByteBuffer.allocateDirect(vertext.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(vertext);
        verextBuffer.position(0);



        colorBuffer = ByteBuffer.allocateDirect(color.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        colorBuffer.position(0);

        mProgram = GLES20.glCreateProgram();
        int vertexShader = Util.createShade(GLES20.GL_VERTEX_SHADER, Util.readTextResourceFromRaw(context, R.raw.color_vertex));
        int fragmentShader = Util.createShade(GLES20.GL_FRAGMENT_SHADER, Util.readTextResourceFromRaw(context, R.raw.color_fragment));
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        GLES20.glUseProgram(mProgram);


        v_position = GLES20.glGetAttribLocation(mProgram, "aPos");
        color_position = GLES20.glGetAttribLocation(mProgram, "v_color");

        int [] temp = new int [1];
        GLES20.glGenBuffers(1, temp, 0);
        dataBuffer = temp[0];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, dataBuffer);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verextBuffer.limit() * 4, verextBuffer, GLES20.GL_STATIC_DRAW);


        GLES20.glEnableVertexAttribArray(v_position);
        GLES20.glVertexAttribPointer(v_position, 3, GLES20.GL_FLOAT, false,
                24, 0);

        GLES20.glEnableVertexAttribArray(color_position);
        GLES20.glVertexAttribPointer(color_position, 3, GLES20.GL_FLOAT, false,
                24, 12);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0f, 0f, 0f, 0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
