#ifndef BLACKBOX_UNIXFILESYSTEMHOOK_H
#define BLACKBOX_UNIXFILESYSTEMHOOK_H

#include "BaseHook.h"

class UnixFileSystemHook : public BaseHook {
public:
    static void init(JNIEnv *env);
};

#endif // BLACKBOX_UNIXFILESYSTEMHOOK_H
