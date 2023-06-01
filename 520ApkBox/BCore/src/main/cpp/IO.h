#ifndef BLACKBOX_IO_H
#define BLACKBOX_IO_H

#if defined(__LP64__)
#define LINKER_PATH_L "/system/bin/linker64"
#define LINKER_PATH_Q "/apex/com.android.runtime/bin/linker64"
#define LIBC_PATH_L "/system/lib64/libc.so"
#define LIBC_PATH_Q "/apex/com.android.runtime/lib64/bionic/libc.so"
#else
#define LINKER_PATH_L "/system/bin/linker"
#define LINKER_PATH_Q "/apex/com.android.runtime/bin/linker"
#define LIBC_PATH_L "/system/lib/libc.so"
#define LIBC_PATH_Q "/apex/com.android.runtime/lib/bionic/libc.so"
#endif

#include <jni.h>

#include <list>
#include <iostream>
#include "BoxCore.h"

using namespace std;

class IO {
public:
    static void init(JNIEnv *env);

    struct RelocateInfo {
        const char *targetPath;
        const char *relocatePath;
    };

    static void addRule(const char *targetPath, const char *relocatePath);

    static void addWhiteList(const char *path);

    static jstring redirectPath(JNIEnv *env, jstring path);

    static jobject redirectPath(JNIEnv *env, jobject path);

    static void replaceFD(JNIEnv *env, jobject fd);

    static void unProtect(const char *libraryName, const char *symbol);
};

#endif // BLACKBOX_IO_H
