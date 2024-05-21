package com.vcore.core.system.pm.installer;

import com.vcore.core.system.pm.BPackageSettings;
import com.vcore.entity.pm.InstallOption;

public interface Executor {
    String TAG = "InstallExecutor";

    int exec(BPackageSettings ps, InstallOption option, int userId);
}
