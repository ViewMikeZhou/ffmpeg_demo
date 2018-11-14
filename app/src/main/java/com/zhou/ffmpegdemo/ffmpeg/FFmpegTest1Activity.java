package com.zhou.ffmpegdemo.ffmpeg;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.zhou.ffmpegdemo.EncodUtil;
import com.zhou.ffmpegdemo.R;

/**
 * Created by zhou on 2018/6/23.
 */

public class FFmpegTest1Activity extends AppCompatActivity {
    static {
        // System.loadLibrary("fmod");
        // System.loadLibrary("fmodL");
        System.loadLibrary("native-lib");
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpegt_test);
        TextView tv = findViewById(R.id.tv);
        tv.setText("test");
        callJni();
        EncodUtil encodUtil = new EncodUtil();
        // String proInfo = encodUtil.init();
        // String avcodecInfo = encodUtil.avcodecInfo();
        String configInfo = encodUtil.configInfo();
        tv.setText(configInfo);

        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        encodUtil.decodeMp4(absolutePath + "/" + "test.mp4");

    }

    public native void callJni();

}
