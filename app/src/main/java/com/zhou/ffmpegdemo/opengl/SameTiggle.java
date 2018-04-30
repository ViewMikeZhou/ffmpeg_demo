package com.zhou.ffmpegdemo.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zhou.ffmpegdemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/** 等边三角形
 * Created by zhou on 2018/4/30.
 */

public class SameTiggle extends AppCompatActivity implements GLSurfaceView.Renderer {
    String vertexCode = "" +
            "attribute vec4 vPosition;" +
            "varying  vec4 vColor;"+
            "attribute vec4 aColor;"+
         //   "uniform vec4 aColor;"+
            "uniform mat4 vMatrix;" +
            "void main() {" +
            "  gl_Position = vMatrix*vPosition;" +
            "vColor = aColor;"+
            "}";

    String fragmentCode = "" +
            "precision mediump float;" +
           // "uniform vec4 vColor;" +
            "varying vec4 vColor;"+
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";

    float [] frustMatri = new float[16];
    float [] cameraMatri = new float[16];
    float [] mmMatri = new float[16];

    static float triangleCoords[] = {
            0.5f, 0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    };

    float color[] = {
            0.0f, 1.0f, 0.0f, 1.0f ,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };

    private int mProgram;
    private FloatBuffer mVertexBuff;
    private FloatBuffer mColorBuff;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.same_tiggle);
        GLSurfaceView glSurface = findViewById(R.id.same_tiggle_surface);
        glSurface.setEGLContextClientVersion(2);
        glSurface.setRenderer(this);

        initBuffer();
    }

    private void initBuffer() {
        mVertexBuff = ByteBuffer.allocateDirect(triangleCoords.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuff.put(triangleCoords);
        mVertexBuff.position(0);

        mColorBuff = ByteBuffer.allocateDirect(color.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColorBuff.put(color);
        mColorBuff.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 0);
        loadProgram(vertexCode, fragmentCode);

    }

    private void loadProgram(String vertexCode, String fragmentCode) {
        int vertexId = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexId, vertexCode);
        GLES20.glCompileShader(vertexId);

        int fragmentId = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentId, fragmentCode);
        GLES20.glCompileShader(fragmentId);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexId);
        GLES20.glAttachShader(mProgram, fragmentId);
        GLES20.glLinkProgram(mProgram);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float ratio = (float) width/ height;
        // 设置透视矩阵
        Matrix.frustumM(frustMatri,0, -ratio, ratio, -1, 1, 3, 7);
        //设置camera位置
        Matrix.setLookAtM(cameraMatri,0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变化举证
        Matrix.multiplyMM(mmMatri,0,frustMatri,0,cameraMatri,0);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        int vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition,3,GLES20.GL_FLOAT,false,0,mVertexBuff);

        int vMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(vMatrix,1,false,mmMatri,0);

     /*   int vColor = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(vColor,1,color,0);
     */

        int aColor = GLES20.glGetAttribLocation(mProgram, "aColor");
        GLES20.glEnableVertexAttribArray(aColor);
        GLES20.glVertexAttribPointer(aColor,3,GLES20.GL_FLOAT,false,0,mColorBuff);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,3);
        GLES20.glDisableVertexAttribArray(vPosition);
    }
}
