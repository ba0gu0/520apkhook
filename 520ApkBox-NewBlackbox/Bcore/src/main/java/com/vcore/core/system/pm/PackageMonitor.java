package com.vcore.core.system.pm;

public interface PackageMonitor {
    void onPackageUninstalled(String packageName, boolean isRemove, int userId);

    void onPackageInstalled(String packageName, int userId);
}
