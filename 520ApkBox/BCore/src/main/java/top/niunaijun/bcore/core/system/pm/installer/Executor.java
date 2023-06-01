package top.niunaijun.bcore.core.system.pm.installer;

import top.niunaijun.bcore.core.system.pm.BPackageSettings;
import top.niunaijun.bcore.entity.pm.InstallOption;

public interface Executor {
    String TAG = "InstallExecutor";

    int exec(BPackageSettings ps, InstallOption option, int userId);
}
