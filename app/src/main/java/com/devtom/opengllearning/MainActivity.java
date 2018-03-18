package com.devtom.opengllearning;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import java.io.File;

public class MainActivity extends Activity {


    private SeekBar xValue;
    private SeekBar yValue;
    private SeekBar zValue;

    private TextView textView;

    private GLSurfaceView.Renderer render;
    private SeekBar mLx;
    private SeekBar mLy;
    private SeekBar mLz;
    private TextView mLtext;


    private ImageView imageView;
    private SimpleDraweeView simpleDraweeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        WebPGLView view = (WebPGLView) this.findViewById(R.id.gl);
        view.setEGLContextClientVersion(2);
        view.setEGLConfigChooser(new MultisampleConfigChooser());
        render = new WebpRender(this);
        view.setRenderer(render);
        view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        Uri uri = Uri.parse("android.resource://com.devtom.opengllearning/drawable/jordan");
        view.setImageUri(uri);

        imageView = (ImageView) this.findViewById(R.id.fresco_view);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();

        simpleDraweeView = (SimpleDraweeView) this.findViewById(R.id.fresco_view2);
        simpleDraweeView.setController(controller);


    }

    public void setImage(Bitmap bitmap) {
        //imageView.setImageBitmap(bitmap);
    }

}
