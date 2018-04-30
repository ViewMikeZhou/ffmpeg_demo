package com.zhou.ffmpegdemo.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zhou.ffmpegdemo.R;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zhou on 2018/4/28.
 */

public class ChapMain extends AppCompatActivity implements GLSurfaceView.Renderer {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chap_main);
        GLSurfaceView surfaceView = findViewById(R.id.gl_surface_view);
        surfaceView.setRenderer(this);
        surfaceView.setEGLContextClientVersion(2);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //告诉系统需要对透视进行修正
        GLES20.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,GL10.GL_NICEST);
        GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
        //启动深度缓存
        GLES20.glEnable(GL10.GL_DEPTH_TEST);


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        float radio = (float) width/height;
       // GLES20.glViewport(0,0,width,height);
        gl.glViewport(0,0,width,height);
        // 设置投影矩阵为透视矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        //重置投影矩阵为透视矩阵 （置为单位矩阵）
        gl.glLoadIdentity();
        //创建一个透视投影矩阵
        gl.glFrustumf(-radio,radio,-1,1,1,0);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

    }
}
