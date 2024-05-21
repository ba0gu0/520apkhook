package com.vcore.core.system.pm.installer;

import java.io.File;
import java.io.IOException;

import com.vcore.core.env.BEnvironment;
import com.vcore.core.system.pm.BPackageSettings;
import com.vcore.entity.pm.InstallOption;
import com.vcore.utils.FileUtils;
import com.vcore.utils.NativeUtils;

public class CopyExecutor implements Executor {

    @Override
    public int exec(BPackageSettings ps, InstallOption option, int userId) {
        try {
            if (!option.isFlag(InstallOption.FLAG_SYSTEM)) {
                NativeUtils.copyNativeLib(new File(ps.pkg.baseCodePath), BEnvironment.getAppLibDir(ps.pkg.packageName));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        if (option.isFlag(InstallOption.FLAG_STORAGE)) {
            // 外部安装
            File origFile = new File(ps.pkg.baseCodePath);
            File newFile = BEnvironment.getBaseApkDir(ps.pkg.packageName);
            try {
                if (option.isFlag(InstallOption.FLAG_URI_FILE)) {
                    boolean b = FileUtils.renameTo(origFile, newFile);
                    if (!b) {
                        FileUtils.copyFile(origFile, newFile);
                    }
                } else {
                    FileUtils.copyFile(origFile, newFile);
                }
                ps.pkg.baseCodePath = newFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return 0;
    }
}
