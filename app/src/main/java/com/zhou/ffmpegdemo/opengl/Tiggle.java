package com.zhou.ffmpegdemo.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zhou.ffmpegdemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zhou on 2018/4/30.
 */

public class Tiggle extends AppCompatActivity implements GLSurfaceView.Renderer {

    private GLSurfaceView mGlSurface;

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    //   "vColor = vec4( 1.0f, 1.0f, 1.0f, 1.0f);" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private int mProgram;

    static float triangleCoords[] = {
            0.5f, 0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    };
    static float color[] = {1.0f, 1.0f, 1.0f, 1.0f};
    private FloatBuffer vertexBuffer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tiggle);
        mGlSurface = findViewById(R.id.tiggle_gl_surface);
        mGlSurface.setEGLContextClientVersion(2);
        mGlSurface.setRenderer(this);
       // mGlSurface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);



        // 1.创建缓存区
        //2.加载program
      //  loadProgram(vertexShaderCode, fragmentShaderCode);
    }

    public int loadShader(int type, String shaderCode){
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void loadProgram(String vertexShaderCode, String fragmentShaderCode) {
        //创建shader句柄
        int vertexId = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexId, vertexShaderCode);
        GLES20.glCompileShader(vertexId);
        int fragmentId = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentId, fragmentShaderCode);
        GLES20.glCompileShader(fragmentId);
        //创建program
        mProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgram, vertexId);
        GLES20.glAttachShader(mProgram, fragmentId);

        GLES20.glLinkProgram(mProgram);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        ByteBuffer bb = ByteBuffer.allocateDirect(
                triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        //创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }


    private int mPositionHandle;
    private int mColorHandle;
    @Override
    public void onDrawFrame(GL10 gl) {
     //   GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // 启用program
       // GLES20.glUseProgram(mProgram);

       /* int vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttrib3fv(vPosition, triangleCoords, 0);
        int vColor = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform1fv(vColor, 1, color, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        GLES20.glEnableVertexAttribArray(vPosition);*/

        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);

        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        //设置绘制三角形的颜色
        //  GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        GLES20.glUniform4fv(mColorHandle,1,color,0);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
    static final int COORDS_PER_VERTEX = 3;
    private final int vertexStride = COORDS_PER_VERTEX * 4;
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
}
