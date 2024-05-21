#ifndef BLACKBOX_LINUXHOOK_H
#define BLACKBOX_LINUXHOOK_H

#include "BaseHook.h"

class LinuxHook : public BaseHook {
public:
    static void init(JNIEnv *env);
};

#endif // BLACKBOX_LINUXHOOK_H
