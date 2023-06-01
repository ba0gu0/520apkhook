// IBXposedManagerService.aidl

package top.niunaijun.bcore.core.system.pm;

import java.util.List;
import top.niunaijun.bcore.entity.pm.InstalledModule;

interface IBXposedManagerService {
    boolean isXPEnable();
    void setXPEnable(boolean enable);
    boolean isModuleEnable(String packageName);
    void setModuleEnable(String packageName, boolean enable);
    List<InstalledModule> getInstalledModules();
}