package com.zhou.ffmpegdemo.opengl;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zhou.ffmpegdemo.R;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.GLEnvironment;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zhou on 2018/5/5.
 */

public class Camera2Preview extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceView mGlSurfaceView;
    int texturId = -1;
    private HandlerThread mStaHandler;
    private Handler mHandler;
    private Handler mMainHander;
    private ImageReader mImageReader;
    private CameraManager mCameraManager;


    String verturCode =
            "attribute vec4 position;\n" +
                    "attribute vec2 texcoord; \n" +
                    "varying vec2 v_texcoord;     \n" +
                    "   gl_Position = position;  \n" +
                    "   v_texcoord = texcoord.xy;  \n";

    String fragmentCode =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying highp vec2 v_texcoord;\n" +
                    "uniform sampler2D yuvTexSampler;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(yuvTexSampler, v_texcoord);\n" +
                    "}\n";
    float[] vercoord = {
            -1.0f, -1.0f,    // 0 top left
            1.0f, -1.0f,    // 1 bottom left
            -1.0f, 1.0f,  // 2 bottom right
            1.0f, 1.0f,    // 3 top right
    };
    float[] texCoord = {
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f
    };
    private FloatBuffer mVerBuffer;
    private FloatBuffer mTexBuffer;
    private int mProgram;

    static {
        System.loadLibrary("filterfw");
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview);
        requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);


        GLEnvironment glEnvironment = new GLEnvironment();
        boolean active = glEnvironment.isActive();
        Log.e("test","isactive:"+active);


        mGlSurfaceView = findViewById(R.id.camera_preview_gl);
        mGlSurfaceView.getHolder().addCallback(this);
     //   mGlSurfaceView.setEGLContextClientVersion(2);
     //   mGlSurfaceView.setRenderer(this);

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        mStaHandler = new HandlerThread("openHandler");
        mStaHandler.start();
        mHandler = new Handler(mStaHandler.getLooper());
        mMainHander = new Handler(getMainLooper());

        if (mImageReader == null) {
            mImageReader = ImageReader.newInstance(1080, 1920, ImageFormat.YUV_420_888, 100);
        }


        mVerBuffer = ByteBuffer.allocateDirect(vercoord.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerBuffer.put(vercoord);
        mVerBuffer.position(0);

        mTexBuffer = ByteBuffer.allocateDirect(texCoord.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTexBuffer.put(texCoord);
        mTexBuffer.position(0);


    }

    public CameraDevice mCamera;
    private CaptureRequest.Builder mCaptureRequestBuild;


    CameraDevice.StateCallback openCallback = new CameraDevice.StateCallback() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCamera = camera;
            try {
                mCaptureRequestBuild = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                Log.e("test", "textId:" + texturId);
              //  SurfaceTexture texture = new SurfaceTexture(texturId);
               // Surface surface = new Surface(texture);
                Surface surface = mGlSurfaceView.getHolder().getSurface();
                mCaptureRequestBuild.addTarget(surface);
                camera.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), creatSessionCallback, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    };


    CameraCaptureSession.StateCallback creatSessionCallback = new CameraCaptureSession.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {

            try {
                session.setRepeatingRequest(mCaptureRequestBuild.build(), null, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

        }
    };


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

    private int creatTexture() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Camera camera = Camera.open();
        try {
            camera.setPreviewDisplay(mGlSurfaceView.getHolder());
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

   /* @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        texturId = creatTexture();
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        try {
            String[] cameraIdList = mCameraManager.getCameraIdList();
            for (String s : cameraIdList) {
                // 0 ,1
                if (s.equals("1")) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mCameraManager.openCamera(s, openCallback, mHandler);
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        loadProgram(verturCode, fragmentCode);

        position = GLES20.glGetAttribLocation(mProgram, "position");
        texcoord = GLES20.glGetAttribLocation(mProgram, "texcoord");
        yuvTexSampler = GLES20.glGetUniformLocation(mProgram, "yuvTexSampler");
    }

    int position;
    int texcoord;
    int yuvTexSampler;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glUseProgram(mProgram);
        GLES20.glVertexAttribPointer(position,2,GLES20.GL_FLOAT,false,0,mVerBuffer);
        GLES20.glEnableVertexAttribArray(position);

        GLES20.glVertexAttribPointer(texcoord,2,GLES20.GL_FLOAT,false,0,mTexBuffer);
        GLES20.glEnableVertexAttribArray(texcoord);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE);
        GLES20.glBindTexture(GLES20.GL_TEXTURE,texturId);

        GLES20.glUniform1i(yuvTexSampler,0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE,0);
        GLES20.glDisableVertexAttribArray(texcoord);
        GLES20.glDisableVertexAttribArray(position);
    }*/
}
