package com.zhou.ffmpegdemo.opengl;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zhou.ffmpegdemo.R;

/**
 * Created by zhou on 2018/4/30.
 */

public class Cube extends AppCompatActivity {

    private GLSurfaceView mGlSurface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cube);
        mGlSurface = findViewById(R.id.cube_gl_surface);
        mGlSurface.setEGLContextClientVersion(2);
        mGlSurface.setRenderer(new BaseRender());
    }
}
