
cmake_minimum_required(VERSION 3.4.1)

set(PATH_TO_MEDIACORE ${CMAKE_SOURCE_DIR}/src/main/cpp)
set(PATH_TO_THIRDPARTY ${PATH_TO_MEDIACORE}/thirdparty)
set(PATH_TO_JNI_LAYER ${CMAKE_SOURCE_DIR}/src/main/jni)

include_directories( ${PATH_TO_MEDIACORE}/)
include_directories(BEFORE ${PATH_TO_THIRDPARTY}/ffmpeg/include/)
set(PATH_TO_PRE_BUILT ${PATH_TO_THIRDPARTY}/prebuilt/${ANDROID_ABI})

file(GLOB FILES_TEST "${PATH_TO_MEDIACORE}/*.cpp")
file(GLOB FILES_JNI_LAYER "${PATH_TO_JNI_LAYER}/*.cpp")

add_library( native-lib
            SHARED
            ${FILES_TEST}
            ${FILES_JNI_LAYER})


find_library( log-lib
                log )



target_link_libraries(native-lib
                         # 引入系统的动态库
                       log
                       android
                       GLESv2
                       EGL
                       z
                       OpenSLES
                        # 引入ffmpeg相关静态库
                      ${PATH_TO_PRE_BUILT}/libavfilter.a
                      ${PATH_TO_PRE_BUILT}/libavformat.a
                      ${PATH_TO_PRE_BUILT}/libavcodec.a
                      ${PATH_TO_PRE_BUILT}/libpostproc.a
                      ${PATH_TO_PRE_BUILT}/libswresample.a
                      ${PATH_TO_PRE_BUILT}/libswscale.a
                      ${PATH_TO_PRE_BUILT}/libavutil.a
                      ${PATH_TO_PRE_BUILT}/libpostproc.a
                      ${PATH_TO_PRE_BUILT}/libfdk-aac.a
                      ${PATH_TO_PRE_BUILT}/libvo-aacenc.a
                      ${PATH_TO_PRE_BUILT}/libx264.a
                      ${PATH_TO_PRE_BUILT}/libsox.a
                       ${log-lib} )