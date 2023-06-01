#ifndef BLACKBOX_SYSTEMPROPERTIESHOOK_H
#define BLACKBOX_SYSTEMPROPERTIESHOOK_H

#include <map>
#include "BaseHook.h"
#include <string>

class SystemPropertiesHook : public BaseHook{
public:
    static void init(JNIEnv *env);
};

#endif // BLACKBOX_SYSTEMPROPERTIESHOOK_H
