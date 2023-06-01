#ifndef BLACKBOX_BOXCORE_H
#define BLACKBOX_BOXCORE_H

#include <jni.h>
#include <unistd.h>

#define NATIVECORE_CLASS "top/niunaijun/bcore/core/NativeCore"

class BoxCore {
public:
    static JavaVM *getJavaVM();
    static int getApiLevel();
    static int getCallingUid(int orig);
    static jstring redirectPathString(JNIEnv *env, jstring path);
    static jobject redirectPathFile(JNIEnv *env, jobject path);
    static void replaceFD(JNIEnv *env, jobject fd);
};


#endif // BLACKBOX_BOXCORE_H
