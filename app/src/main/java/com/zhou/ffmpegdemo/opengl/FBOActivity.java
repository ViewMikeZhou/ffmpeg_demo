package com.zhou.ffmpegdemo.opengl;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zhou.ffmpegdemo.R;
import com.zhou.ffmpegdemo.opengl.filter.AFilter;
import com.zhou.ffmpegdemo.opengl.filter.Gl2Utils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zhou on 2018/5/12.
 */

public class FBOActivity extends AppCompatActivity implements GLSurfaceView.Renderer {
    String vertexCode = "attribute vec4 vPosition;\n" +
            "attribute vec2 vCoord;\n" +
            "uniform mat4 vMatrix;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main(){\n" +
            "    gl_Position = vMatrix*vPosition;\n" +
            "    textureCoordinate = vCoord;\n" +
            "}";
    String fragmentCode = "precision mediump float;\n" +
            "varying vec2 textureCoordinate;\n" +
            "uniform sampler2D vTexture;\n" +
            "void main() {\n" +
            "    vec4 color=texture2D( vTexture, textureCoordinate);\n" +
            "    float rgb=color.g;\n" +
            "    vec4 c=vec4(rgb,rgb,rgb,color.a);\n" +
            "    gl_FragColor = c;\n" +
            "}";
    private AFilter mFilter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbo);
        GLSurfaceView glSurface = findViewById(R.id.fbo_gl_surface);
        glSurface.setEGLContextClientVersion(2);
        glSurface.setRenderer(this);

        mFilter = new AFilter();


    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFilter.createProgram(vertexCode,fragmentCode);
        mFilter.setMatrix(Gl2Utils.flip(Gl2Utils.getOriginalMatrix(),false,true));
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}
