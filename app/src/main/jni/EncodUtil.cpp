//
// Created by Administrator on 2018/4/26.
//
#include <libavformat/avformat.h>
#include "jni.h"
#include "com_zhou_ffmpegdemo_EncodUtil.h"

// 初始化 ffmpeg解码相关
JNIEXPORT void JNICALL Java_com_zhou_ffmpegdemo_EncodUtil_init
        (JNIEnv *, jobject) {
    AVCodec *avCodec;
    av_register_all();
    avcodec_find_decoder(CODEC_ID_H264);
    AVCodecContext *avCodecContext = avcodec_alloc_context3(avCodec);
    
}