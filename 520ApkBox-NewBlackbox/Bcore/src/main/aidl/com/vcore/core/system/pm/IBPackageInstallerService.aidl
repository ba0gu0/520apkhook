// IBPackageInstallerService.aidl
package com.vcore.core.system.pm;

import  com.vcore.core.system.pm.BPackageSettings;
import com.vcore.entity.pm.InstallOption;

// Declare any non-default types here with import statements
interface IBPackageInstallerService {
    int installPackageAsUser(in BPackageSettings ps, int userId);
    int uninstallPackageAsUser(in BPackageSettings ps, boolean removeApp, int userId);
    int clearPackage(in BPackageSettings ps, int userId);
    int updatePackage(in BPackageSettings ps);
}
