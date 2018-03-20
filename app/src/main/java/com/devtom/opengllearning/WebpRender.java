package com.devtom.opengllearning;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by liuwei64 on 2018/3/16.
 */

public class WebpRender implements GLSurfaceView.Renderer {

    private static final float[] VERTEX = {   // in counterclockwise order:
            1, 1, 0,   // top right
            -1, 1, 0,  // top left
            -1, -1, 0, // bottom left
            1, -1, 0,  // bottom right
    };
    private static final short[] VERTEX_INDEX = {
            0, 1, 2, 0, 2, 3
    };
    private static final float[] TEX_VERTEX = {   // in clockwise order:
            1f, 0,  // bottom right
            0, 0,  // bottom left
            0, 1f,  // top left
            1f, 1f,  // top right
    };

    private final Context mContext;
    private final FloatBuffer mVertexBuffer;
    private final FloatBuffer mTexVertexBuffer;
    private final ShortBuffer mVertexIndexBuffer;


    private int mProgram;
    private int mPositionHandle;

    private int mTexCoordHandle;
    private int mTexSamplerHandle;
    private int mTexName = -1;
    private int matrixHandle;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private Drawable drawable;
    private Bitmap cacheBitmap;

    private int contentWidth;
    private int contentHeight;
    private int width;
    private int height;

    private boolean shouldChangeRespect;


    public WebpRender(final Context context) {
        mContext = context;
        mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(VERTEX);
        mVertexBuffer.position(0);

        mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(VERTEX_INDEX);
        mVertexIndexBuffer.position(0);

        mTexVertexBuffer = ByteBuffer.allocateDirect(TEX_VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(TEX_VERTEX);
        mTexVertexBuffer.position(0);
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public void setContentSize(int width, int height) {
        mTexName = -1;
        if (cacheBitmap != null && width == this.contentWidth && this.contentHeight == height) {
            return;
        }

        if (cacheBitmap != null) {
            cacheBitmap.recycle();
        }

        cacheBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        this.contentWidth = width;
        this.contentHeight = height;
        shouldChangeRespect = true;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        mProgram = GLES20.glCreateProgram();
        int vertexShader = Util.createShade(GLES20.GL_VERTEX_SHADER, Util.readTextResourceFromRaw(mContext, R.raw.texture_vertex));
        int fragmentShader = Util.createShade(GLES20.GL_FRAGMENT_SHADER, Util.readTextResourceFromRaw(mContext, R.raw.texture_fragment));
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
        GLES20.glUseProgram(mProgram);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_texCoord");
        mTexSamplerHandle = GLES20.glGetUniformLocation(mProgram, "s_texture");
        matrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                12, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0,
                mTexVertexBuffer);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
        shouldChangeRespect = true;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        if (cacheBitmap != null) {
            cacheBitmap.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(cacheBitmap);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            if (drawable != null) {
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            }

            if (shouldChangeRespect) {
                int w = contentWidth;
                int h = contentHeight;
                float sWH = w / (float) h;
                float sWidthHeight = width / (float) height;
                if (width > height) {
                    if (sWH > sWidthHeight) {
                        Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 7);
                    } else {
                        Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 7);
                    }
                } else {
                    if (sWH > sWidthHeight) {
                        Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 7);
                    } else {
                        Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 7);
                    }
                }
                //设置相机位置
                Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
                //计算变换矩阵
                Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
                shouldChangeRespect = false;
            }

            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mMVPMatrix, 0);
            GLES20.glUniform1i(mTexSamplerHandle, 0);

            mTexName = Util.loadTexture(cacheBitmap, mTexName, false);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexName);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                    GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
        } else {
            Log.e("Null", "Content not ready...");
        }

    }
}
