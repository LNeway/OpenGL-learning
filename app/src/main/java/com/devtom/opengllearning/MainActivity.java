package com.devtom.opengllearning;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.SeekBar;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener{


    private SeekBar xValue;
    private SeekBar yValue;
    private SeekBar zValue;

    private ThreeDimensionRender render;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GLSurfaceView view = (GLSurfaceView) this.findViewById(R.id.gl);
        view.setEGLContextClientVersion(2);
        view.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        render = new ThreeDimensionRender(this);
        view.setRenderer(render);
        view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        xValue = (SeekBar) this.findViewById(R.id.x);
        yValue = (SeekBar) this.findViewById(R.id.y);
        zValue = (SeekBar) this.findViewById(R.id.z);

        zValue.setOnSeekBarChangeListener(this);
        xValue.setOnSeekBarChangeListener(this);
        yValue.setOnSeekBarChangeListener(this);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == xValue) {
            render.eyeX = progress - 50;
        } else  if (seekBar == yValue) {
            render.eyeY = progress - 50;
        } else {
            render.eyeZ = progress - 50;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
