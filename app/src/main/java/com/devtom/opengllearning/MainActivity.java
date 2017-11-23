package com.devtom.opengllearning;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener {


    private SeekBar xValue;
    private SeekBar yValue;
    private SeekBar zValue;

    private TextView textView;

    private ThreeDimensionRender render;
    private SeekBar mLx;
    private SeekBar mLy;
    private SeekBar mLz;
    private TextView mLtext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        GLSurfaceView view = (GLSurfaceView) this.findViewById(R.id.gl);
        view.setEGLContextClientVersion(2);
        view.setEGLConfigChooser(new MultisampleConfigChooser());

        render = new ThreeDimensionRender(this);
        view.setRenderer(render);
        view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        xValue = (SeekBar) this.findViewById(R.id.x);
        yValue = (SeekBar) this.findViewById(R.id.y);
        zValue = (SeekBar) this.findViewById(R.id.z);

        textView = (TextView) this.findViewById(R.id.text);

        zValue.setOnSeekBarChangeListener(this);
        xValue.setOnSeekBarChangeListener(this);
        yValue.setOnSeekBarChangeListener(this);
        mLx.setOnSeekBarChangeListener(this);
        mLy.setOnSeekBarChangeListener(this);
        mLz.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == xValue) {
            render.eyeX = progress - 50;
        } else if (seekBar == yValue) {
            render.eyeY = progress - 50;
        } else if (seekBar == zValue) {
            render.eyeZ = progress - 50;
        } else if (seekBar == mLx) {
            render.upX = progress - 50;
        } else if (seekBar == mLy) {
            render.upY = progress - 50;
        } else  {
            render.upZ = progress - 50;
        }

        textView.setText("(" + render.eyeX + " , " + render.eyeY + " , " + render.eyeZ + ")");
        mLtext.setText("(" + render.upX + " , " + render.upY + " , " + render.upZ + ")");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void initView() {
        mLx = (SeekBar) findViewById(R.id.lx);
        mLy = (SeekBar) findViewById(R.id.ly);
        mLz = (SeekBar) findViewById(R.id.lz);
        mLtext = (TextView) findViewById(R.id.ltext);
    }
}
