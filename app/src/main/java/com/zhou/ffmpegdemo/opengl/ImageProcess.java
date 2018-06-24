package com.zhou.ffmpegdemo.opengl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zhou.ffmpegdemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zhou on 2018/5/1.
 */

public class ImageProcess extends AppCompatActivity implements GLSurfaceView.Renderer, View.OnClickListener {
    // shader的编写后面一定要加分号 ；
    String vertexCode = "" +
            "attribute vec4 vPosition;\n" +
            "attribute vec2 vCoordinate;\n" +       // 定点坐标
            "uniform mat4 vMatrix;\n" +
            "\n" +
            "varying vec2 aCoordinate;\n" +
            "varying vec4 gPosition;\n" +
            "varying vec4 aPos;\n" +
            "\n" +
            "void main(){\n" +
            "    gl_Position=vMatrix*vPosition;\n" +
            "    aCoordinate=vCoordinate;\n" +
            "     aPos = vPosition ;\n" +
            "      gPosition= gl_Position;\n" +
            "}";
    /*String fragmentCode = "" +
            "precision mediump float;\n" +
            "\n" +
            "uniform sampler2D vTexture;\n" +       // 纹理采样
            "varying vec2 aCoordinate;\n" +
            "\n" +
            "void main(){\n" +
            "    gl_FragColor=texture2D(vTexture,aCoordinate);\n" +
            "}";*/

    String fragmentCode = "precision mediump float;\n" +
            "\n" +
            "uniform sampler2D vTexture;\n" +
            "uniform int vChangeType;\n" +
            "uniform vec3 vChangeColor;\n" +
            "uniform int vIsHalf;\n" +
            "uniform float uXY;      //屏幕宽高比\n" +
            "\n" +
            "varying vec4 gPosition;\n" +
            "\n" +
            "varying vec2 aCoordinate;\n" +
            "varying vec4 aPos;\n" +
            "\n" +
            "void modifyColor(vec4 color){\n" +
            "    color.r=max(min(color.r,1.0),0.0);\n" +
            "    color.g=max(min(color.g,1.0),0.0);\n" +
            "    color.b=max(min(color.b,1.0),0.0);\n" +
            "    color.a=max(min(color.a,1.0),0.0);\n" +
            "}\n" +
            "\n" +
            "void main(){\n" +
            "    vec4 nColor=texture2D(vTexture,aCoordinate);\n" +
            "    if(aPos.x>0.0||vIsHalf==0){\n" +
            "        if(vChangeType==1){    //黑白图片\n" +
            "            float c=nColor.r*vChangeColor.r+nColor.g*vChangeColor.g+nColor.b*vChangeColor.b;\n" +
            "            gl_FragColor=vec4(c,c,c,nColor.a);\n" +
            "        }else if(vChangeType==2){    //简单色彩处理，冷暖色调、增加亮度、降低亮度等\n" +
            "            vec4 deltaColor=nColor+vec4(vChangeColor,0.0);\n" +
            "            modifyColor(deltaColor);\n" +
            "            gl_FragColor=deltaColor;\n" +
            "        }else if(vChangeType==3){    //模糊处理\n" +
            "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.r,aCoordinate.y-vChangeColor.r));\n" +
            "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.r,aCoordinate.y+vChangeColor.r));\n" +
            "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.r,aCoordinate.y-vChangeColor.r));\n" +
            "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.r,aCoordinate.y+vChangeColor.r));\n" +
            "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.g,aCoordinate.y-vChangeColor.g));\n" +
            "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.g,aCoordinate.y+vChangeColor.g));\n" +
            "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.g,aCoordinate.y-vChangeColor.g));\n" +
            "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.g,aCoordinate.y+vChangeColor.g));\n" +
            "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.b,aCoordinate.y-vChangeColor.b));\n" +
            "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.b,aCoordinate.y+vChangeColor.b));\n" +
            "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.b,aCoordinate.y-vChangeColor.b));\n" +
            "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.b,aCoordinate.y+vChangeColor.b));\n" +
            "            nColor/=13.0;\n" +
            "            gl_FragColor=nColor;\n" +
            "        }else if(vChangeType==4){  //放大镜效果\n" +
            "            float dis=distance(vec2(gPosition.x,gPosition.y/uXY),vec2(vChangeColor.r,vChangeColor.g));\n" +
            "            if(dis<vChangeColor.b){\n" +
            "                nColor=texture2D(vTexture,vec2(aCoordinate.x/2.0+0.25,aCoordinate.y/2.0+0.25));\n" +
            "            }\n" +
            "            gl_FragColor=nColor;\n" +
            "        }else{\n" +
            "            gl_FragColor=nColor;\n" +
            "        }\n" +
            "    }else{\n" +
            "        gl_FragColor=nColor;\n" +
            "    }\n" +
            "}";


    float[] vertexCoordinate = {
            -1.0f, 1.0f,    /* 0.0f,*/
            -1.0f, -1.0f,   /* 0.0f,*/
            1.0f, 1.0f,     /* 0.0f,*/
            1.0f, -1.0f   /* 0.0f*/

    };
    float[] textureCoordinate = {
            0.0f, 0.0f,/* 0.0f,*/
            0.0f, 1.0f,/* 0.0f,*/
            1.0f, 0.0f,/* 0.0f,*/
            1.0f, 1.0f/* 0.0f*/

    };

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    int mProgram;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    private Bitmap mBitmap;
    private int mTextureId;
    private int[] mTexture;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_process);
        GLSurfaceView glSurface = findViewById(R.id.image_gl_surface);
        glSurface.setEGLContextClientVersion(2);
        glSurface.setRenderer(this);
        findView();

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fengj);
        mVertexBuffer = ByteBuffer.allocateDirect(vertexCoordinate.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(vertexCoordinate).position(0);

        mTextureBuffer = ByteBuffer.allocateDirect(textureCoordinate.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureBuffer.put(textureCoordinate).position(0);
    }

    private void findView() {
        findViewById(R.id.bt1).setOnClickListener(this);
        findViewById(R.id.bt2).setOnClickListener(this);
        findViewById(R.id.bt3).setOnClickListener(this);
        findViewById(R.id.bt4).setOnClickListener(this);
        findViewById(R.id.bt_helf).setOnClickListener(this);
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
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
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mTextureId = createTexture(null);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        loadProgram(vertexCode, fragmentCode);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        //  uXY=sWidthHeight;
        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 5);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 5);
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 5);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 5);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);


    }

    int changeType;
    float[] changeColor = new float[]{0.0f, 0.0f, 0.0f};
    int ishalf;
    float uxy;

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);

        int vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        int vCoordinate = GLES20.glGetAttribLocation(mProgram, "vCoordinate");
        GLES20.glEnableVertexAttribArray(vCoordinate);
        GLES20.glVertexAttribPointer(vCoordinate, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);

        int vMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mMVPMatrix, 0);


        int vTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
        GLES20.glUniform1i(vTexture, 0);

        int vChangeType = GLES20.glGetUniformLocation(mProgram, "vChangeType");
        GLES20.glUniform1i(vChangeType, changeType);

        int vChangeColor = GLES20.glGetUniformLocation(mProgram, "vChangeColor");
        GLES20.glUniform3fv(vChangeColor, 1, changeColor, 0);

        int vIsHalf = GLES20.glGetUniformLocation(mProgram, "vIsHalf");
        GLES20.glUniform1i(vIsHalf, ishalf);

        int uXY = GLES20.glGetUniformLocation(mProgram, "uXY");
        GLES20.glUniform1f(uXY, uxy);


        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(vPosition);
        GLES20.glDisableVertexAttribArray(vCoordinate);
        GLES20.glUseProgram(0);
    }


    private int createTexture(Bitmap bitmap) {
        mTexture = new int[1];
        if (mBitmap != null && !mBitmap.isRecycled()) {
            //生成纹理
            GLES20.glGenTextures(1, mTexture, 0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE, mTexture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap == null ? mBitmap : bitmap, 0);
            return mTexture[0];
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt1:
                changeType = 0;
                changeColor = new float[]{0.0f, 0.0f, 0.4f};
                break;
            case R.id.bt2:
                changeType = 1;
                changeColor = new float[]{0.299f, 0.587f, 0.114f};
                break;
            case R.id.bt3:
                changeType = 2;
                changeColor = new float[]{0.0f, 0.0f, 0.1f};
                break;
            case R.id.bt4:
                changeType = 3;
                changeColor = new float[]{0.006f, 0.004f, 0.002f};
                break;
            case R.id.bt_helf:
                ishalf = ishalf == 0 ? 1 : 0;
                break;

        }
    }


}
