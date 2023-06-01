#ifndef BLACKBOX_BASEHOOK_H
#define BLACKBOX_BASEHOOK_H

#include <jni.h>
#include <Log.h>

class BaseHook {
public:
    static void init(JNIEnv *env);
};

#endif // BLACKBOX_BASEHOOK_H
