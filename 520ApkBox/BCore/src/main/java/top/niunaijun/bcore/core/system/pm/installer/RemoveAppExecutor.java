package top.niunaijun.bcore.core.system.pm.installer;

import top.niunaijun.bcore.core.env.BEnvironment;
import top.niunaijun.bcore.core.system.pm.BPackageSettings;
import top.niunaijun.bcore.entity.pm.InstallOption;
import top.niunaijun.bcore.utils.FileUtils;

public class RemoveAppExecutor implements Executor {
    @Override
    public int exec(BPackageSettings ps, InstallOption option, int userId) {
        FileUtils.deleteDir(BEnvironment.getAppDir(ps.pkg.packageName));
        return 0;
    }
}
