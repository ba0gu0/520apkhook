package com.vcore.core.system.pm.installer;

import com.vcore.core.env.BEnvironment;
import com.vcore.core.system.pm.BPackageSettings;
import com.vcore.entity.pm.InstallOption;
import com.vcore.utils.FileUtils;

public class CreatePackageExecutor implements Executor {

    @Override
    public int exec(BPackageSettings ps, InstallOption option, int userId) {
        FileUtils.deleteDir(BEnvironment.getAppDir(ps.pkg.packageName));
        FileUtils.mkdirs(BEnvironment.getAppDir(ps.pkg.packageName));
        FileUtils.mkdirs(BEnvironment.getAppLibDir(ps.pkg.packageName));
        return 0;
    }
}
