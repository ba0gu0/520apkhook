// IBXposedManagerService.aidl

package com.vcore.core.system.pm;

import java.util.List;
import com.vcore.entity.pm.InstalledModule;

interface IBXposedManagerService {
    boolean isXPEnable();
    void setXPEnable(boolean enable);
    boolean isModuleEnable(String packageName);
    void setModuleEnable(String packageName, boolean enable);
    List<InstalledModule> getInstalledModules();
}