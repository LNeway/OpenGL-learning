package com.devtom.opengllearning;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GLSurfaceView view = (GLSurfaceView) this.findViewById(R.id.gl);
        view.setEGLContextClientVersion(2);
        view.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        view.setRenderer(new ColorfulRender(this));
        view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
