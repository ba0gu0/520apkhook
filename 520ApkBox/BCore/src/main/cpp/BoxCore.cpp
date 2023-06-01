#include "BoxCore.h"
#include "Log.h"
#include "IO.h"
#include <jni.h>
#include "JniHook/JniHook.h"
#include "Hook/VMClassLoaderHook.h"
#include "Hook/UnixFileSystemHook.h"
#include "Hook/LinuxHook.h"
#include "Hook/SystemPropertiesHook.h"
#include "Hook/BinderHook.h"
#include "Hook/RuntimeHook.h"

struct {
    JavaVM *vm;
    jclass NativeCoreClass;
    jmethodID getCallingUidId;
    jmethodID redirectPathString;
    jmethodID redirectPathFile;
    int api_level;
} VMEnv;

JNIEnv *getEnv() {
    JNIEnv *env;
    VMEnv.vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6);
    return env;
}

JNIEnv *ensureEnvCreated() {
    JNIEnv *env = getEnv();
    if (env == nullptr) {
        VMEnv.vm->AttachCurrentThread(&env, nullptr);
    }
    return env;
}

int BoxCore::getCallingUid(int orig) {
    JNIEnv *env = ensureEnvCreated();
    return env->CallStaticIntMethod(VMEnv.NativeCoreClass, VMEnv.getCallingUidId, orig);
}

jstring BoxCore::redirectPathString(JNIEnv *env, jstring path) {
    env = ensureEnvCreated();
    return (jstring) env->CallStaticObjectMethod(VMEnv.NativeCoreClass, VMEnv.redirectPathString, path);
}

jobject BoxCore::redirectPathFile(JNIEnv *env, jobject path) {
    env = ensureEnvCreated();
    return env->CallStaticObjectMethod(VMEnv.NativeCoreClass, VMEnv.redirectPathFile, path);
}

int BoxCore::getApiLevel() {
    return VMEnv.api_level;
}

JavaVM *BoxCore::getJavaVM() {
    return VMEnv.vm;
}

void nativeHook(JNIEnv *env) {
    BaseHook::init(env);
    UnixFileSystemHook::init(env);
    LinuxHook::init(env);
    VMClassLoaderHook::init(env);

    // SystemPropertiesHook会引起小米k40，安卓11上的抖音崩溃
    // SystemPropertiesHook::init(env);
    RuntimeHook::init(env);
    BinderHook::init(env);
}

void hideXposed(JNIEnv *env, jclass clazz) {
    ALOGD("Hiding Xposed!");
    VMClassLoaderHook::hideXposed();
}

void init(JNIEnv *env, jobject clazz, jint api_level) {
    ALOGD("NativeCore init.");
    VMEnv.api_level = api_level;
    VMEnv.NativeCoreClass = (jclass) env->NewGlobalRef(env->FindClass(NATIVECORE_CLASS));
    VMEnv.getCallingUidId = env->GetStaticMethodID(VMEnv.NativeCoreClass, "getCallingUid", "(I)I");
    VMEnv.redirectPathString = env->GetStaticMethodID(VMEnv.NativeCoreClass, "redirectPath", "(Ljava/lang/String;)Ljava/lang/String;");
    VMEnv.redirectPathFile = env->GetStaticMethodID(VMEnv.NativeCoreClass, "redirectPath", "(Ljava/io/File;)Ljava/io/File;");
    JniHook::InitJniHook(env, api_level);
}

// IO类添加重定向规则
void addIORule(JNIEnv *env, jclass clazz, jstring target_path, jstring relocate_path) {
    IO::addRule(env->GetStringUTFChars(target_path, JNI_FALSE),env->GetStringUTFChars(relocate_path, JNI_FALSE));
}

// IO类添加白名单规则
void addWhiteList(JNIEnv *env, jclass clazz, jstring path) {
    IO::addWhiteList(env->GetStringUTFChars(path, JNI_FALSE));
}

void enableIO(JNIEnv *env, jclass clazz) {
    nativeHook(env);
}

static JNINativeMethod gMethods[] = {
        {"hideXposed", "()V", (void *) hideXposed},
        {"addIORule", "(Ljava/lang/String;Ljava/lang/String;)V", (void *) addIORule},
        {"enableIO", "()V", (void *) enableIO},
        {"init", "(I)V", (void *) init},
        {"addWhiteList", "(Ljava/lang/String;)V", (void *) addWhiteList},
};

int registerNativeMethods(JNIEnv *env, const char *className,JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == nullptr) {
        return JNI_FALSE;
    }

    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

int registerNatives(JNIEnv *env) {
    if (!registerNativeMethods(env, NATIVECORE_CLASS, gMethods, sizeof(gMethods) / sizeof(gMethods[0]))) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

void registerMethod(JNIEnv *jenv) {
    registerNatives(jenv);
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    VMEnv.vm = vm;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_EVERSION;
    }
    registerMethod(env);
    return JNI_VERSION_1_6;
}
