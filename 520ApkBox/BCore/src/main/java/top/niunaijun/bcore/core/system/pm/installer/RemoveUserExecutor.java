package top.niunaijun.bcore.core.system.pm.installer;

import top.niunaijun.bcore.core.env.BEnvironment;
import top.niunaijun.bcore.core.system.pm.BPackageSettings;
import top.niunaijun.bcore.entity.pm.InstallOption;
import top.niunaijun.bcore.utils.FileUtils;

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
