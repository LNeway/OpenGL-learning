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
        -1, 1, 0,
        1, 1, 0,
        1, -1, 0,
    };

    private final short position [] = {0, 1, 2};


    private FloatBuffer verextBuffer = null;
    private ShortBuffer shortBuffer = null;

    private int v_position;
    private int colorUniform;


    public ColorfulRender(Context context) {
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
        int vertexShader = Util.createShade(GLES20.GL_VERTEX_SHADER, Util.readTextResourceFromRaw(context, R.raw.vertex));
        int fragmentShader = Util.createShade(GLES20.GL_FRAGMENT_SHADER, Util.readTextResourceFromRaw(context, R.raw.fragment));
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        GLES20.glUseProgram(mProgram);


        v_position = GLES20.glGetAttribLocation(mProgram, "aPos");
        GLES20.glEnableVertexAttribArray(v_position);
        GLES20.glVertexAttribPointer(v_position, 3, GLES20.GL_FLOAT, false,
                12, verextBuffer);


        colorUniform = GLES20.glGetUniformLocation(mProgram, "color");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        long colorValue = System.currentTimeMillis() % 256;
        GLES20.glUniform4f(colorUniform, 1f, Math.abs(colorValue - 256), colorValue, 1f);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
