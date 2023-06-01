package top.niunaijun.bcore.core.system;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.core.env.AppSystemEnv;
import top.niunaijun.bcore.core.env.BEnvironment;
import top.niunaijun.bcore.core.system.accounts.BAccountManagerService;
import top.niunaijun.bcore.core.system.am.BActivityManagerService;
import top.niunaijun.bcore.core.system.am.BJobManagerService;
import top.niunaijun.bcore.core.system.location.BLocationManagerService;
import top.niunaijun.bcore.core.system.notification.BNotificationManagerService;
import top.niunaijun.bcore.core.system.os.BStorageManagerService;
import top.niunaijun.bcore.core.system.pm.BPackageInstallerService;
import top.niunaijun.bcore.core.system.pm.BPackageManagerService;
import top.niunaijun.bcore.core.system.pm.BXposedManagerService;
import top.niunaijun.bcore.core.system.user.BUserHandle;
import top.niunaijun.bcore.core.system.user.BUserManagerService;
import top.niunaijun.bcore.entity.pm.InstallOption;

public class BlackBoxSystem {
    private final List<ISystemService> mServices = new ArrayList<>();
    private final static AtomicBoolean isStartup = new AtomicBoolean(false);

    private static final class SBlackBoxSystemHolder {
        static final BlackBoxSystem sBlackBoxSystem = new BlackBoxSystem();
    }

    public static BlackBoxSystem getSystem() {
        return SBlackBoxSystemHolder.sBlackBoxSystem;
    }

    public void startup() {
        if (isStartup.getAndSet(true)) {
            return;
        }

        BEnvironment.load();
        mServices.add(BPackageManagerService.get());
        mServices.add(BUserManagerService.get());
        mServices.add(BActivityManagerService.get());
        mServices.add(BJobManagerService.get());
        mServices.add(BStorageManagerService.get());
        mServices.add(BPackageInstallerService.get());
        mServices.add(BXposedManagerService.get());
        mServices.add(BProcessManagerService.get());
        mServices.add(BAccountManagerService.get());
        mServices.add(BLocationManagerService.get());
        mServices.add(BNotificationManagerService.get());

        for (ISystemService service : mServices) {
            service.systemReady();
        }

        List<String> preInstallPackages = AppSystemEnv.getPreInstallPackages();
        for (String preInstallPackage : preInstallPackages) {
            try {
                if (!BPackageManagerService.get().isInstalled(preInstallPackage, BUserHandle.USER_ALL)) {
                    PackageInfo packageInfo = BlackBoxCore.getPackageManager().getPackageInfo(preInstallPackage, 0);
                    BPackageManagerService.get().installPackageAsUser(packageInfo.applicationInfo.sourceDir, InstallOption.installBySystem(), BUserHandle.USER_ALL);
                }
            } catch (PackageManager.NameNotFoundException ignored) { }
        }
    }
}
