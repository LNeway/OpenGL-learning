package com.devtom.opengllearning;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by tomliu on 2017/8/26.
 */

public class TextureRender implements GLSurfaceView.Renderer {

    private Context context;

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

    private int v_position;
    private int tex_position;
    private int texture;
    private int textureUniform;
    private int matrix;


    private float texure [] = {
            0f, 0f,
            1f, 0f,
            1f, 1f,
            0f, 1f };

    public TextureRender(Context context){
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
        GLES20.glVertexAttribPointer(tex_position, 2, GLES20.GL_FLOAT, false, 0, texBuffer);

        textureUniform = GLES20.glGetUniformLocation(program, "u_samplerTexture");
        GLES20.glUniform1i(textureUniform, 0);

        try {
            texture = createTexture(BitmapFactory.decodeStream(context.getAssets().open("texture.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        //设置相机位置
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        // 用 glDrawElements 来绘制，mVertexIndexBuffer 指定了顶点绘制顺序
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, position.length,
                GLES20.GL_UNSIGNED_SHORT, shortBuffer);
    }


    public static int createTexture(Bitmap bitmap) {
        int[] texture = new int[1];
        if (bitmap != null && !bitmap.isRecycled()) {
            // 生成纹理
            GLES20.glGenTextures(1, texture, 0);
            // 生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            // 设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            //            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            //            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // 设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            // 设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // 根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            return texture[0];
        }

        return 0;
    }

}
