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
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.facebook.drawee.view.DraweeHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.CountDownLatch;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by liuwei64 on 2018/3/16.
 */

public class WebpRender implements GLSurfaceView.Renderer {
    private Context context;

    private DraweeHolder draweeHolder;

    private int width;
    private int height;
    private volatile Bitmap cacheBitmap;

    private int program;
    private final float vertext [] = {
            1, 1, 0,   // top right
            -1, 1, 0,  // top left
            -1, -1, 0, // bottom left
            1, -1, 0,  // bottom right
    };

    private final short position [] = {0, 1, 2, 2, 3, 0};


    private FloatBuffer verextBuffer = null;
    private ShortBuffer shortBuffer = null;
    private FloatBuffer texBuffer;
    private IntBuffer pixs;

    private int v_position;
    private int tex_position;
    private int texture = -1;
    private int textureUniform;

    private float texure [] = {
            0f, 0f,
            1f, 0f,
            1f, 1f,
            0f, 1f };

    public WebpRender(Context context, DraweeHolder holder) {
        this.context =  context;
        verextBuffer = ByteBuffer.allocateDirect(vertext.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertext);
        verextBuffer.position(0);

        shortBuffer = ByteBuffer.allocateDirect(position.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer().put(position);
        shortBuffer.position(0);

        texBuffer = ByteBuffer.allocateDirect(texure.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(texure);
        texBuffer.position(0);
        this.draweeHolder = holder;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        program = GLES20.glCreateProgram();
        int vertex = Util.createShade(GLES20.GL_VERTEX_SHADER,
                Util.readTextResourceFromRaw(context, R.raw.texture_vertex));
        int fragment = Util.createShade(GLES20.GL_FRAGMENT_SHADER,
                Util.readTextResourceFromRaw(context, R.raw.texture_fragment));

        GLES20.glAttachShader(program, vertex);
        GLES20.glAttachShader(program, fragment);
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);

        v_position = GLES20.glGetAttribLocation(program, "aPos");
        GLES20.glEnableVertexAttribArray(v_position);
        GLES20.glVertexAttribPointer(v_position, 3, GLES20.GL_FLOAT, false, 12, verextBuffer);

        tex_position = GLES20.glGetAttribLocation(program, "v_texCoord");
        GLES20.glEnableVertexAttribArray(tex_position);
        GLES20.glVertexAttribPointer(tex_position, 2, GLES20.GL_FLOAT, false, 8, texBuffer);

        textureUniform = GLES20.glGetUniformLocation(program, "u_samplerTexture");
        GLES20.glUniform1i(textureUniform, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        //设置相机位置
        this.width = width;
        this.height = height;
        cacheBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        pixs = IntBuffer.allocate(height * width);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Drawable drawable = draweeHolder.getTopLevelDrawable();
        try {
            MainUITask.getPix(context, cacheBitmap, drawable, width, height, pixs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BITS);
        texture = Util.loadTexture(pixs, width, height, texture);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, position.length,
                GLES20.GL_UNSIGNED_SHORT, shortBuffer);
    }

    private static class MainUITask {
        private static Handler handler = new Handler(Looper.getMainLooper());
        public static void getPix(final Context  context, final @NonNull Bitmap cacheBitmap, final Drawable drawable,
                                  final int width, final int height, final IntBuffer intBuffer) throws InterruptedException {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    cacheBitmap.eraseColor(Color.TRANSPARENT);
                    Canvas canvas = new Canvas(cacheBitmap);
                    canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);
                    cacheBitmap.getPixels(intBuffer.array(), 0, width, 0, 0, width, height);
                    Bitmap bitmap = Bitmap.createBitmap(intBuffer.array(), width, height, Bitmap.Config.ARGB_8888);
                    MainActivity activity = (MainActivity) context;
                    activity.setImage(bitmap);
                    countDownLatch.countDown();
                }
            });

            countDownLatch.await();
        }
    }
}
