package com.devtom.opengllearning;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.image.ImageInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by liuwei64 on 2018/3/16.
 */

public class WebPGLView extends GLSurfaceView  {

    private DraweeHolder mDraweeHolder;
    private Drawable currentDrawable;
    private WebpRender render;

    public WebPGLView(Context context) {
        super(context);
        init(context);
    }

    public WebPGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        GenericDraweeHierarchyBuilder hierarchyBuilder = new GenericDraweeHierarchyBuilder(getResources());
        mDraweeHolder = DraweeHolder.create(hierarchyBuilder.build(), context);
        mDraweeHolder.getTopLevelDrawable().setCallback(this);
    }


    @Override
    public void setRenderer(Renderer renderer) {
        super.setRenderer(renderer);
        this.render = (WebpRender) renderer;
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        render.setDrawable(drawable);
        requestRender();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDraweeHolder.onDetach();
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        mDraweeHolder.onDetach();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDraweeHolder.onAttach();
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        mDraweeHolder.onAttach();
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        if (who == mDraweeHolder.getTopLevelDrawable()) {
            return true;
        }
        return super.verifyDrawable(who);
    }

    public void setImageUri(String imageUri) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(imageUri)
                .setAutoPlayAnimations(true)
                .build();
        mDraweeHolder.setController(controller);
        mDraweeHolder.getTopLevelDrawable();
    }

    public void setImageUri(Uri imageUri) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(imageUri)
                .setAutoPlayAnimations(true)
                .build();
        mDraweeHolder.setController(controller);
    }

    public DraweeHolder getDraweeHolder() {
        return mDraweeHolder;
    }
}
