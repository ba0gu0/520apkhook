package top.niunaijun.bcore.core.system.pm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;

import java.util.HashSet;
import java.util.Set;

import black.android.content.pm.ApplicationInfoL;
import black.android.content.pm.ApplicationInfoN;
import black.android.content.pm.SigningInfo;
import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.core.env.AppSystemEnv;
import top.niunaijun.bcore.core.env.BEnvironment;
import top.niunaijun.bcore.entity.pm.InstallOption;
import top.niunaijun.bcore.utils.ArrayUtils;
import top.niunaijun.bcore.utils.FileUtils;
import top.niunaijun.bcore.utils.compat.BuildCompat;

@SuppressLint({"SdCardPath", "NewApi"})
public class PackageManagerCompat {
    public static PackageInfo generatePackageInfo(BPackageSettings ps, int flags, BPackageUserState state, int userId) {
        if (ps == null) {
            return null;
        }

        BPackage p = ps.pkg;
        if (p != null) {
            PackageInfo packageInfo = null;
            try {
                packageInfo = generatePackageInfo(p, flags, 0, 0, state, userId);
            } catch (Throwable ignored) { }
            return packageInfo;
        }
        return null;
    }

    public static PackageInfo generatePackageInfo(BPackage p, int flags, long firstInstallTime, long lastUpdateTime, BPackageUserState state, int userId) {
        if (checkUseInstalledOrHidden(state, p.applicationInfo)) {
            return null;
        }

        PackageInfo pi = new PackageInfo();
        pi.packageName = p.packageName;
        pi.versionCode = p.mVersionCode;
        pi.versionName = p.mVersionName;
        pi.sharedUserId = p.mSharedUserId;
        pi.sharedUserLabel = p.mSharedUserLabel;
        pi.applicationInfo = generateApplicationInfo(p, flags, state, userId);

        pi.firstInstallTime = firstInstallTime;
        pi.lastUpdateTime = lastUpdateTime;
        if (!p.requestedPermissions.isEmpty()) {
            String[] requestedPermissions = new String[p.requestedPermissions.size()];
            p.requestedPermissions.toArray(requestedPermissions);
            pi.requestedPermissions = requestedPermissions;
        }

        if ((flags & PackageManager.GET_GIDS) != 0) {
            pi.gids = new int[]{};
        }

        if ((flags & PackageManager.GET_CONFIGURATIONS) != 0) {
            int N = p.configPreferences != null ? p.configPreferences.size() : 0;
            if (N > 0) {
                pi.configPreferences = new ConfigurationInfo[N];
                p.configPreferences.toArray(pi.configPreferences);
            }

            N = p.reqFeatures != null ? p.reqFeatures.size() : 0;
            if (N > 0) {
                pi.reqFeatures = new FeatureInfo[N];
                p.reqFeatures.toArray(pi.reqFeatures);
            }
        }

        if ((flags & PackageManager.GET_ACTIVITIES) != 0) {
            pi.activities = null;
            final int N = p.activities.size();

            if (N > 0) {
                int num = 0;
                final ActivityInfo[] res = new ActivityInfo[N];
                for (int i = 0; i < N; i++) {
                    final BPackage.Activity a = p.activities.get(i);
                    res[num++] = generateActivityInfo(a, flags, state, userId);
                }

                pi.activities = ArrayUtils.trimToSize(res, num);
            }
        }

        if ((flags & PackageManager.GET_RECEIVERS) != 0) {
            pi.receivers = null;
            final int N = p.receivers.size();

            if (N > 0) {
                int num = 0;
                final ActivityInfo[] res = new ActivityInfo[N];
                for (int i = 0; i < N; i++) {
                    final BPackage.Activity a = p.receivers.get(i);
                    res[num++] = generateActivityInfo(a, flags, state, userId);
                }

                pi.receivers = ArrayUtils.trimToSize(res, num);
            }
        }

        if ((flags & PackageManager.GET_SERVICES) != 0) {
            pi.services = null;
            final int N = p.services.size();
            if (N > 0) {
                int num = 0;
                final ServiceInfo[] res = new ServiceInfo[N];
                for (int i = 0; i < N; i++) {
                    final BPackage.Service s = p.services.get(i);
                    res[num++] = generateServiceInfo(s, flags, state, userId);
                }

                pi.services = ArrayUtils.trimToSize(res, num);
            }
        }

        if ((flags & PackageManager.GET_PROVIDERS) != 0) {
            pi.providers = null;
            final int N = p.providers.size();

            if (N > 0) {
                int num = 0;
                final ProviderInfo[] res = new ProviderInfo[N];
                for (int i = 0; i < N; i++) {
                    final BPackage.Provider pr = p.providers.get(i);
                    ProviderInfo providerInfo = generateProviderInfo(pr, flags, state, userId);
                    if (providerInfo != null) {
                        res[num++] = providerInfo;
                    }
                }

                pi.providers = ArrayUtils.trimToSize(res, num);
            }
        }

        if ((flags & PackageManager.GET_INSTRUMENTATION) != 0) {
            pi.instrumentation = null;
            int N = p.instrumentation.size();
            if (N > 0) {
                pi.instrumentation = new InstrumentationInfo[N];
                for (int i = 0; i < N; i++) {
                    pi.instrumentation[i] = generateInstrumentationInfo(p.instrumentation.get(i), flags);
                }
            }
        }

        if ((flags & PackageManager.GET_PERMISSIONS) != 0) {
            pi.permissions = null;
            int N = p.permissions.size();
            if (N > 0) {
                pi.permissions = new PermissionInfo[N];
                for (int i = 0; i < N; i++) {
                    pi.permissions[i] = generatePermissionInfo(p.permissions.get(i), flags);
                }
            }

            pi.requestedPermissions = null;
            N = p.requestedPermissions.size();
            if (N > 0) {
                pi.requestedPermissions = new String[N];
                pi.requestedPermissionsFlags = new int[N];
                for (int i = 0; i < N; i++) {
                    final String perm = p.requestedPermissions.get(i);
                    pi.requestedPermissions[i] = perm;
                }
            }
        }

        PackageInfo base = null;
        try {
            base = BlackBoxCore.getPackageManager().getPackageInfo(p.packageName, flags);
        } catch (PackageManager.NameNotFoundException ignored) { }
        if (base != null) {
            if (base.splitNames != null) {
                pi.splitNames = base.splitNames;
            }
        }

        if ((flags & PackageManager.GET_SIGNATURES) != 0) {
            if (base == null) {
                pi.signatures = p.mSignatures;
            } else {
                pi.signatures = base.signatures;
            }
        }

        if (BuildCompat.isPie()) {
            if ((flags & PackageManager.GET_SIGNING_CERTIFICATES) != 0) {
                if (base == null) {
                    black.android.content.pm.PackageParser.SigningDetails.signatures.set(null, p.mSigningDetails.signatures);
                    pi.signingInfo = SigningInfo._new.newInstance();
                } else {
                    pi.signingInfo = base.signingInfo;
                }
            }
        }
        return pi;
    }

    public static ActivityInfo generateActivityInfo(BPackage.Activity a, int flags, BPackageUserState state, int userId) {
        if (checkUseInstalledOrHidden(state, a.info.applicationInfo)) {
            return null;
        }
        // Make shallow copies so we can store the metadata safely
        ActivityInfo ai = new ActivityInfo(a.info);
        ai.metaData = a.metaData;
        ai.processName = BPackageManagerService.fixProcessName(ai.packageName, ai.processName);
        ai.applicationInfo = generateApplicationInfo(a.owner, flags, state, userId);
        return ai;
    }

    public static ServiceInfo generateServiceInfo(BPackage.Service s, int flags, BPackageUserState state, int userId) {
        if (checkUseInstalledOrHidden(state, s.info.applicationInfo)) {
            return null;
        }
        // Make shallow copies so we can store the metadata safely
        ServiceInfo si = new ServiceInfo(s.info);
        si.metaData = s.metaData;
        si.processName = BPackageManagerService.fixProcessName(si.packageName, si.processName);
        si.applicationInfo = generateApplicationInfo(s.owner, flags, state, userId);
        return si;
    }

    public static ProviderInfo generateProviderInfo(BPackage.Provider p, int flags, BPackageUserState state, int userId) {
        if (checkUseInstalledOrHidden(state, p.info.applicationInfo)) {
            return null;
        }
        // Make shallow copies so we can store the metadata safely
        ProviderInfo pi = new ProviderInfo(p.info);
        if (pi.authority == null) {
            return null;
        }

        pi.metaData = p.metaData;
        pi.processName = BPackageManagerService.fixProcessName(pi.packageName, pi.processName);
        if ((flags & PackageManager.GET_URI_PERMISSION_PATTERNS) == 0) {
            pi.uriPermissionPatterns = null;
        }
        pi.applicationInfo = generateApplicationInfo(p.owner, flags, state, userId);
        return pi;
    }

    public static PermissionInfo generatePermissionInfo(BPackage.Permission p, int flags) {
        if (p == null) {
            return null;
        }

        if ((flags & PackageManager.GET_META_DATA) == 0) {
            return p.info;
        }
        PermissionInfo pi = new PermissionInfo(p.info);
        pi.metaData = p.metaData;
        return pi;
    }

    public static InstrumentationInfo generateInstrumentationInfo(BPackage.Instrumentation i, int flags) {
        if (i == null) {
            return null;
        }

        if ((flags & PackageManager.GET_META_DATA) == 0) {
            return i.info;
        }
        InstrumentationInfo ii = new InstrumentationInfo(i.info);
        ii.metaData = i.metaData;
        return ii;
    }

    public static ApplicationInfo generateApplicationInfo(BPackage p, int flags, BPackageUserState state, int userId) {
        if (checkUseInstalledOrHidden(state, p.applicationInfo)) {
            return null;
        }

        ApplicationInfo baseApplication;
        try {
            baseApplication = BlackBoxCore.getPackageManager().getApplicationInfo(BlackBoxCore.getHostPkg(), flags);
        } catch (Exception e) {
            return null;
        }

        String sourceDir = p.baseCodePath;
        if (p.applicationInfo == null) {
            p.applicationInfo = BlackBoxCore.getPackageManager().getPackageArchiveInfo(sourceDir, 0).applicationInfo;
        }

        ApplicationInfo ai = new ApplicationInfo(p.applicationInfo);
        if ((flags & PackageManager.GET_META_DATA) != 0) {
            ai.metaData = p.mAppMetaData;
        }

        ai.dataDir = BEnvironment.getDataDir(ai.packageName, userId).getAbsolutePath();
        if (!p.installOption.isFlag(InstallOption.FLAG_SYSTEM)) {
            ai.nativeLibraryDir = BEnvironment.getAppLibDir(ai.packageName).getAbsolutePath();
        }
        ai.processName = BPackageManagerService.fixProcessName(p.packageName, ai.packageName);
        ai.publicSourceDir = sourceDir;
        ai.sourceDir = sourceDir;
        ai.uid = p.mExtras.appId;

        if (BuildCompat.isL()) {
            ApplicationInfoL.primaryCpuAbi.set(ai, Build.CPU_ABI);
            ApplicationInfoL.scanPublicSourceDir.set(ai, ApplicationInfoL.scanPublicSourceDir.get(baseApplication));
            ApplicationInfoL.scanSourceDir.set(ai, ApplicationInfoL.scanSourceDir.get(baseApplication));
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            ai.deviceProtectedDataDir = BEnvironment.getDeDataDir(p.packageName, userId).getAbsolutePath();

            if (ApplicationInfoN.deviceProtectedDataDir != null) {
                ApplicationInfoN.deviceProtectedDataDir.set(ai, ai.deviceProtectedDataDir);
            }

            if (ApplicationInfoN.credentialEncryptedDataDir != null) {
                ApplicationInfoN.credentialEncryptedDataDir.set(ai, ai.dataDir);
            }

            if (ApplicationInfoN.deviceProtectedDataDir != null) {
                ApplicationInfoN.deviceProtectedDataDir.set(ai, ai.deviceProtectedDataDir);
            }

            if (ApplicationInfoN.credentialProtectedDataDir != null) {
                ApplicationInfoN.credentialProtectedDataDir.set(ai, ai.dataDir);
            }
        }
        fixJar(ai);
        return ai;
    }

    private static boolean checkUseInstalledOrHidden(BPackageUserState state, ApplicationInfo appInfo) {
        if (AppSystemEnv.isBlackPackage(appInfo.packageName)) {
            return true;
        }
        // Returns false if the package is hidden system app until installed.
        return !state.installed || state.hidden;
    }

    private static void fixJar(ApplicationInfo info) {
        String APACHE_LEGACY_JAR = "/system/framework/org.apache.http.legacy.boot.jar";
        String APACHE_LEGACY_JAR_Q = "/system/framework/org.apache.http.legacy.jar";
        Set<String> sharedLibraryFileList = new HashSet<>();

        if (BuildCompat.isQ()) {
            if (!FileUtils.isExist(APACHE_LEGACY_JAR_Q)) {
                sharedLibraryFileList.add(APACHE_LEGACY_JAR);
            } else {
                sharedLibraryFileList.add(APACHE_LEGACY_JAR_Q);
            }
        } else {
            sharedLibraryFileList.add(APACHE_LEGACY_JAR);
        }
        info.sharedLibraryFiles = sharedLibraryFileList.toArray(new String[]{});
    }

    public static Resources getResources(Context context, ApplicationInfo appInfo) {
        BPackageSettings ps = BPackageManagerService.get().getBPackageSetting(appInfo.packageName);
        if (ps != null) {
            AssetManager assets = black.android.content.res.AssetManager._new.newInstance();
            black.android.content.res.AssetManager.addAssetPath.call(assets, ps.pkg.baseCodePath);

            Resources hostRes = context.getResources();
            return new Resources(assets, hostRes.getDisplayMetrics(), hostRes.getConfiguration());
        }
        return null;
    }
}
