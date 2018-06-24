package com.zhou.ffmpegdemo.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zhou on 2018/4/30.
 */

public class BaseRender implements GLSurfaceView.Renderer {


    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "varying  vec4 vColor;" +
                    "attribute vec4 aColor;" +
                    "void main() {" +
                    " gl_Position = vMatrix*vPosition;" +
                    "vColor = aColor;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 vColor;"+
                    "void main() {" +
                    //   "vColor = vec4( 1.0f, 1.0f, 1.0f, 1.0f);" +
                    "  gl_FragColor = vColor;" +
                    "}";

    static float triangleCoords[] = {
            0.5f, 0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    };

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    int mProgram;

    final float cubePositions[] = {
            -1.0f, 1.0f, 1.0f,    //正面左上0
            -1.0f, -1.0f, 1.0f,   //正面左下1
            1.0f, -1.0f, 1.0f,    //正面右下2
            1.0f, 1.0f, 1.0f,     //正面右上3
            -1.0f, 1.0f, -1.0f,    //反面左上4
            -1.0f, -1.0f, -1.0f,   //反面左下5
            1.0f, -1.0f, -1.0f,    //反面右下6
            1.0f, 1.0f, -1.0f,     //反面右上7
    };

    float color[] = {
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
    };

    final short index[] = {
            0, 3, 2, 0, 2, 1,    //正面
            0, 1, 5, 0, 5, 4,    //左面
            0, 7, 3, 0, 4, 7,    //上面
            6, 7, 4, 6, 4, 5,    //后面
            6, 3, 7, 6, 2, 3,    //右面
            6, 5, 1, 6, 1, 2     //下面
    };
    private final ShortBuffer mIndexBuffer;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];


    public BaseRender() {

        mVertexBuffer = ByteBuffer.allocateDirect(cubePositions.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(cubePositions);
        mVertexBuffer.position(0);


        mColorBuffer = ByteBuffer.allocateDirect(color.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColorBuffer.put(color);
        mColorBuffer.position(0);


        mIndexBuffer = ByteBuffer.allocateDirect(index.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndexBuffer.put(index);
        mIndexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        loadProgram(vertexShaderCode, fragmentShaderCode);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }


    public void reload() {
        mVertexBuffer.clear();
        mVertexBuffer = ByteBuffer.allocateDirect(cubePositions.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(cubePositions);
        mVertexBuffer.position(0);
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
        //计算宽高比
        float ratio = (float) width / height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    int mPositionHandle;
    int mColorHandle;


    /**
     * 此方法一直调用
     *
     * @param gl
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);

        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, mVertexBuffer);
        int aColor = GLES20.glGetAttribLocation(mProgram, "aColor");
        GLES20.glEnableVertexAttribArray(aColor);
        GLES20.glVertexAttribPointer(aColor,4,GLES20.GL_FLOAT,false,0,mColorBuffer);

        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        /*//绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);*/

        int vMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mMVPMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
