#ifndef XPOSED_LOG_H
#define XPOSED_LOG_H

#include <android/log.h>

enum logType {
    DEBUG = 3,
    ERROR = 6,
    INFO  = 4,
    WARN  = 5
};

#define TAG "Xposed"

#define LOGD(...) ((void) __android_log_print(DEBUG, TAG, __VA_ARGS__))
#define LOGE(...) ((void) __android_log_print(ERROR, TAG, __VA_ARGS__))
#define LOGI(...) ((void) __android_log_print(INFO,  TAG, __VA_ARGS__))
#define LOGW(...) ((void) __android_log_print(WARN,  TAG, __VA_ARGS__))

#endif // XPOSED_LOG_H
