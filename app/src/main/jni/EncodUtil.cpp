//
// Created by Administrator on 2018/4/26.
//

#include "jni.h"
#include "com_zhou_ffmpegdemo_EncodUtil.h"
#include "common/commonUtil.h"
// 初始化 ffmpeg解码相关
#define LOG_TAG "EncodUtil"
struct URLProtocol;

JNIEXPORT jstring JNICALL Java_com_zhou_ffmpegdemo_EncodUtil_init
        (JNIEnv *env, jobject) {
    char info[40000] = {0};
    /*av_register_all();
    struct URLProtocol *pup = NULL;
    //Input
    struct URLProtocol **p_temp = &pup;
    avio_enum_protocols((void **) p_temp, 0);
    while ((*p_temp) != NULL) {
        sprintf(info, "%s[In ][%10s]\n", info, avio_enum_protocols((void **) p_temp, 0));
    }
    pup = NULL;
    //Output
    avio_enum_protocols((void **) p_temp, 1);
    while ((*p_temp) != NULL) {
        sprintf(info, "%s[Out][%10s]\n", info, avio_enum_protocols((void **) p_temp, 1));
    }
    LOGE("%s", info);*/
    av_register_all();
    avcodec_register_all();


    AVCodec *pCodec = avcodec_find_decoder(CODEC_ID_H264);
    if (!pCodec) {
        LOGE("codec not find ");
    }

    AVCodecContext *codecCtx = avcodec_alloc_context3(pCodec);
    if (!codecCtx) {
        LOGE("codec context fail");
    }

    int ret = avcodec_open2(codecCtx, pCodec, NULL);
    if (ret == -1) {
        LOGE("codec open fail");
    }

    AVFrame *pvideoFrame = av_frame_alloc();

    /**
     *  开始解码
     */
    AVPacket avPacket;
    int gotPicPtr = 0;
    int result = 0;
    av_init_packet(&avPacket);
    //avPacket.data =
    //avPacket.size =
    //解码操作
    avcodec_decode_video2(codecCtx, pvideoFrame, &gotPicPtr, &avPacket);

    return env->NewStringUTF(info);
}


JNIEXPORT jstring JNICALL
Java_com_zhou_ffmpegdemo_EncodUtil_avcodecInfo(JNIEnv *env, jobject instance) {

    char info[40000] = {0};

    av_register_all();

    AVCodec *c_temp = av_codec_next(NULL);

    while (c_temp != NULL) {
        if (c_temp->decode != NULL) {
            sprintf(info, "%s[Dec]", info);
        } else {
            sprintf(info, "%s[Enc]", info);
        }
        switch (c_temp->type) {
            case AVMEDIA_TYPE_VIDEO:
                sprintf(info, "%s[Video]", info);
                break;
            case AVMEDIA_TYPE_AUDIO:
                sprintf(info, "%s[Audio]", info);
                break;
            default:
                sprintf(info, "%s[Other]", info);
                break;
        }
        sprintf(info, "%s[%10s]\n", info, c_temp->name);


        c_temp = c_temp->next;
    }
    return env->NewStringUTF(info);

}

JNIEXPORT jstring JNICALL
Java_com_zhou_ffmpegdemo_EncodUtil_configInfo(JNIEnv *env, jobject instance) {
    char info[10000] = {0};
    av_register_all();

    sprintf(info, "%s\n", avcodec_configuration());

    //LOGE("%s", info);
    return env->NewStringUTF(info);
}


JNIEXPORT void JNICALL
Java_com_zhou_ffmpegdemo_EncodUtil_decodeMp4(JNIEnv *env, jobject instance, jstring filePath_) {
    const char *filePath = env->GetStringUTFChars(filePath_, 0);
    LOGE("%s", filePath);

    env->ReleaseStringUTFChars(filePath_, filePath);
}