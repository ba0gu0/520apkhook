package com.vcore.core.system.pm.installer;

import com.vcore.core.env.BEnvironment;
import com.vcore.core.system.pm.BPackageSettings;
import com.vcore.entity.pm.InstallOption;
import com.vcore.utils.FileUtils;

public class RemoveAppExecutor implements Executor {
    @Override
    public int exec(BPackageSettings ps, InstallOption option, int userId) {
        FileUtils.deleteDir(BEnvironment.getAppDir(ps.pkg.packageName));
        return 0;
    }
}
