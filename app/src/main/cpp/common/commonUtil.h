//
// Created by Administrator on 2018/6/23.
//
#include "android/log.h"

#ifndef FFMPEGTEST_COMMONUTIL_H
#define FFMPEGTEST_COMMONUTIL_H

#endif //FFMPEGTEST_COMMONUTIL_H

//#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)