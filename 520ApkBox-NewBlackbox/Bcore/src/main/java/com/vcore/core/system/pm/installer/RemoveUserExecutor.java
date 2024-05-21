package com.vcore.core.system.pm.installer;

import com.vcore.core.env.BEnvironment;
import com.vcore.core.system.pm.BPackageSettings;
import com.vcore.entity.pm.InstallOption;
import com.vcore.utils.FileUtils;

public class RemoveUserExecutor implements Executor {

    @Override
    public int exec(BPackageSettings ps, InstallOption option, int userId) {
        String packageName = ps.pkg.packageName;
		
        FileUtils.deleteDir(BEnvironment.getDataDir(packageName, userId));
        FileUtils.deleteDir(BEnvironment.getDeDataDir(packageName, userId));
        FileUtils.deleteDir(BEnvironment.getExternalDataDir(packageName, userId));
        return 0;
    }
}
