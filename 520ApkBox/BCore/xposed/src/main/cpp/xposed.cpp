#include <jni.h>
#include <dobby.h>
#include <lsplant.hpp>
#include <sys/sysconf.h>
#include <sys/mman.h>
#include "Utils/elf_util.h"

void *inlineHooker(void *targetFunc, void *replaceFunc) {
    auto pageSize = sysconf(_SC_PAGE_SIZE);
    auto funcAddress = ((uintptr_t) targetFunc) & (-pageSize);
    mprotect((void *) funcAddress, pageSize, PROT_READ | PROT_WRITE | PROT_EXEC);

    void *originalFunc;
    if (DobbyHook(targetFunc, (dobby_dummy_func_t) replaceFunc, (dobby_dummy_func_t *) &originalFunc) == RS_SUCCESS) {
        return originalFunc;
    }
    return nullptr;
}

bool inlineUnHooker(void *originalFunc) {
    return DobbyDestroy(originalFunc) == RT_SUCCESS;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_de_robv_android_xposed_XposedBridge_hook0(JNIEnv *env, jclass clazz, jobject context, jobject originalMethod, jobject callbackMethod) {
    return lsplant::Hook(env, originalMethod, context, callbackMethod);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_de_robv_android_xposed_XposedBridge_unhook0(JNIEnv *env, jclass clazz, jobject targetMember) {
    return lsplant::UnHook(env, targetMember);
}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    LSPosed::ElfImg art("libart.so");
    lsplant::InitInfo initInfo {
            .inline_hooker = inlineHooker,
            .inline_unhooker = inlineUnHooker,
            .art_symbol_resolver = [&art](std::string_view symbol) -> void *{
                return art.getSymbAddress(symbol);
            },
            .art_symbol_prefix_resolver = [&art](auto symbol) {
                return art.getSymbPrefixFirstOffset(symbol);
            },
    };
    lsplant::Init(env, initInfo);
    return JNI_VERSION_1_6;
}