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
    av_register_all();
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
    LOGE("%s", info);
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