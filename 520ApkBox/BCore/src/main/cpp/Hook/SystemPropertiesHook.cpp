#include <dobby.h>
#include "SystemPropertiesHook.h"
#include "IO.h"
#include "BoxCore.h"
#import "JniHook/JniHook.h"
#include "Log.h"

static std::map<std::string, std::string> prop_map;

HOOK_JNI(jstring, native_get, JNIEnv *env, jobject obj, jstring key, jstring def) {
    const char *key_str = env->GetStringUTFChars(key, JNI_FALSE);
    const char *def_str = env->GetStringUTFChars(def, JNI_FALSE);
    if (key == nullptr || def == nullptr) {
        return orig_native_get(env, obj, key, def);
    }

    auto ret = prop_map.find(key_str);
    if (ret != prop_map.end()) {
        const char *ret_value = ret->second.c_str();
        return env->NewStringUTF(ret_value);
    }

    env->ReleaseStringUTFChars(key, key_str);
    env->ReleaseStringUTFChars(key, def_str);
    return orig_native_get(env, obj, key, def);
}

HOOK_JNI(int, __system_property_get, const char *name, char *value) {
    if (name == nullptr || value == nullptr) {
        return orig___system_property_get(name, value);
    }

    ALOGD(name, value);
    auto ret = prop_map.find(name);
    if (ret != prop_map.end()) {
        const char *ret_value = ret->second.c_str();
        strcpy(value, ret_value);
        return strlen(ret_value);
    }
    return orig___system_property_get(name, value);
}

void SystemPropertiesHook::init(JNIEnv *env) {
    prop_map.insert(map<std::string, std::string>::value_type("ro.product.board", "umi"));
    prop_map.insert(map<std::string, std::string>::value_type("ro.product.brand", "Xiaomi"));
    prop_map.insert(map<std::string, std::string>::value_type("ro.product.device", "umi"));
    prop_map.insert(map<std::string, std::string>::value_type("ro.build.display.id","QKQ1.191117.002 test-keys"));
    prop_map.insert(map<std::string, std::string>::value_type("ro.build.host", "c5-miui-ota-bd074.bj"));
    prop_map.insert(map<std::string, std::string>::value_type("ro.build.id", "QKQ1.191117.002"));
    prop_map.insert(map<std::string, std::string>::value_type("ro.product.manufacturer", "Xiaomi"));
    prop_map.insert(map<std::string, std::string>::value_type("ro.product.model", "Mi 10"));
    prop_map.insert(map<std::string, std::string>::value_type("ro.product.name", "umi"));
    prop_map.insert(map<std::string, std::string>::value_type("ro.build.tags", "release-keys"));
    prop_map.insert(map<std::string, std::string>::value_type("ro.build.type", "user"));
    prop_map.insert(map<std::string, std::string>::value_type("ro.build.user", "builder"));
    JniHook::HookJniFun(env, "android/os/SystemProperties","native_get", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
                        (void *) new_native_get, (void **) (&orig_native_get), true);

    IO::unProtect("libc.so", "__system_property_get");
    void *systemPropertyGetAddress = DobbySymbolResolver("libc.so", "__system_property_get");
    if (systemPropertyGetAddress) {
        DobbyHook(systemPropertyGetAddress, (void *) new___system_property_get, (void **) &orig___system_property_get);
    }
}