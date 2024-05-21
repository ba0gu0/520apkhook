package com.vcore.fake.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.VersionedPackage;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import black.android.app.ActivityThread;
import black.android.app.ApplicationPackageManager;
import black.android.app.ContextImpl;
import com.vcore.BlackBoxCore;
import com.vcore.app.BActivityThread;
import com.vcore.core.env.AppSystemEnv;
import com.vcore.core.system.pm.BPackageManagerService;
import com.vcore.core.system.pm.BPackageSettings;
import com.vcore.fake.hook.BinderInvocationStub;
import com.vcore.fake.hook.MethodHook;
import com.vcore.fake.hook.ProxyMethod;
import com.vcore.fake.service.base.PkgMethodProxy;
import com.vcore.utils.MethodParameterUtils;
import com.vcore.utils.Slog;
import com.vcore.utils.compat.BuildCompat;
import com.vcore.utils.compat.ParceledListSliceCompat;

public class IPackageManagerProxy extends BinderInvocationStub {
    public static final String TAG = "PackageManagerProxy";

    public IPackageManagerProxy() {
        super(ActivityThread.sPackageManager.get().asBinder());
    }

    @Override
    protected Object getWho() {
        return ActivityThread.sPackageManager.get();
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        ActivityThread.sPackageManager.set(proxyInvocation);
        replaceSystemService("package");

        Object systemContext = ActivityThread.getSystemContext.call(BlackBoxCore.mainThread());
        PackageManager packageManager = ContextImpl.mPackageManager.get(systemContext);
        if (packageManager != null) {
            try {
                ApplicationPackageManager.mPM.set(packageManager, proxyInvocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new PkgMethodProxy("getPackageUid"));
        addMethodHook(new PkgMethodProxy("canRequestPackageInstalls"));
        if (BuildCompat.isOreo()) {
            addMethodHook("getPackageInfoVersioned", new MethodHook() {
                @Override
                protected Object hook(Object who, Method method, Object[] args) throws Throwable {
                    VersionedPackage versionedPackage = (VersionedPackage) args[0];
                    String packageName = versionedPackage.getPackageName();
                    PackageInfo packageInfo;

                    if (BuildCompat.isT()) {
                        long flags = (long) args[1];
                        packageInfo = BlackBoxCore.getBPackageManager().getPackageInfo(packageName, Math.toIntExact(flags), BActivityThread.getUserId());
                    } else {
                        int flags = (int) args[1];
                        packageInfo = BlackBoxCore.getBPackageManager().getPackageInfo(packageName, flags, BActivityThread.getUserId());
                    }

                    if (packageInfo != null) {
                        return packageInfo;
                    }

                    if (AppSystemEnv.isOpenPackage(packageName)) {
                        return method.invoke(who, args);
                    }
                    return null;
                }
            });
        }
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("resolveIntent")
    public static class ResolveIntent extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[0];
            String resolvedType = (String) args[1];
            ResolveInfo resolveInfo;

            if (BuildCompat.isT()) {
                long flags = (long) args[2];
                resolveInfo = BlackBoxCore.getBPackageManager().resolveIntent(intent, resolvedType, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[2];
                resolveInfo = BlackBoxCore.getBPackageManager().resolveIntent(intent, resolvedType, flags, BActivityThread.getUserId());
            }

            if (resolveInfo != null) {
                return resolveInfo;
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("resolveService")
    public static class ResolveService extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[0];
            String resolvedType = (String) args[1];
            ResolveInfo resolveInfo;

            if (BuildCompat.isT()) {
                long flags = (long) args[2];
                resolveInfo = BlackBoxCore.getBPackageManager().resolveService(intent, Math.toIntExact(flags), resolvedType, BActivityThread.getUserId());
            } else {
                int flags = (int) args[2];
                resolveInfo = BlackBoxCore.getBPackageManager().resolveService(intent, flags, resolvedType, BActivityThread.getUserId());
            }

            if (resolveInfo != null) {
                return resolveInfo;
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getPackageInfo")
    public static class GetPackageInfo extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String packageName = (String) args[0];
            PackageInfo packageInfo;

            if (BuildCompat.isT()) {
                long flags = (long) args[1];
                packageInfo = BlackBoxCore.getBPackageManager().getPackageInfo(packageName, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[1];
                packageInfo = BlackBoxCore.getBPackageManager().getPackageInfo(packageName, flags, BActivityThread.getUserId());
            }

            if (packageInfo != null) {
                return packageInfo;
            }

            if (AppSystemEnv.isOpenPackage(packageName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("getProviderInfo")
    public static class GetProviderInfo extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            ProviderInfo providerInfo;

            if (BuildCompat.isT()) {
                long flags = (long) args[1];
                providerInfo = BlackBoxCore.getBPackageManager().getProviderInfo(componentName, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[1];
                providerInfo = BlackBoxCore.getBPackageManager().getProviderInfo(componentName, flags, BActivityThread.getUserId());
            }

            if (providerInfo != null) {
                return providerInfo;
            }

            if (AppSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("getReceiverInfo")
    public static class GetReceiverInfo extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            ActivityInfo receiverInfo;

            if (BuildCompat.isT()) {
                long flags = (long) args[1];
                receiverInfo = BlackBoxCore.getBPackageManager().getReceiverInfo(componentName, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[1];
                receiverInfo = BlackBoxCore.getBPackageManager().getReceiverInfo(componentName, flags, BActivityThread.getUserId());
            }

            if (receiverInfo != null) {
                return receiverInfo;
            }

            if (AppSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("getActivityInfo")
    public static class GetActivityInfo extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            ActivityInfo activityInfo;

            if (BuildCompat.isT()) {
                long flags = (long) args[1];
                activityInfo = BlackBoxCore.getBPackageManager().getActivityInfo(componentName, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[1];
                activityInfo = BlackBoxCore.getBPackageManager().getActivityInfo(componentName, Math.toIntExact(flags), BActivityThread.getUserId());
            }

            if (activityInfo != null) {
                return activityInfo;
            }

            if (AppSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("getServiceInfo")
    public static class GetServiceInfo extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            ServiceInfo serviceInfo;

            if (BuildCompat.isT()) {
                long flags = (long) args[1];
                serviceInfo = BlackBoxCore.getBPackageManager().getServiceInfo(componentName, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[1];
                serviceInfo = BlackBoxCore.getBPackageManager().getServiceInfo(componentName, flags, BActivityThread.getUserId());
            }

            if (serviceInfo != null) {
                return serviceInfo;
            }

            if (AppSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("getInstalledApplications")
    public static class GetInstalledApplications extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            List<ApplicationInfo> installedApplications;

            if (BuildCompat.isT()) {
                long flags = (long) args[0];
                installedApplications = BlackBoxCore.getBPackageManager().getInstalledApplications(Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[0];
                installedApplications = BlackBoxCore.getBPackageManager().getInstalledApplications(flags, BActivityThread.getUserId());
            }
            return ParceledListSliceCompat.create(installedApplications);
        }
    }

    @ProxyMethod("queryIntentActivities")
    public static class QueryIntentActivities extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[0];
            String resolvedType = (String) args[1];
            List<ResolveInfo> intentActivities;

            if (BuildCompat.isT()) {
                long flags = (long) args[2];
                intentActivities = BlackBoxCore.getBPackageManager().queryIntentActivities(intent, Math.toIntExact(flags), resolvedType, BActivityThread.getUserId());
            } else {
                int flags = (int) args[2];
                intentActivities = BlackBoxCore.getBPackageManager().queryIntentActivities(intent, flags, resolvedType, BActivityThread.getUserId());
            }
            return ParceledListSliceCompat.create(intentActivities);
        }
    }

    @ProxyMethod("getInstalledPackages")
    public static class GetInstalledPackages extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            List<PackageInfo> installedPackages;

            if (BuildCompat.isT()) {
                long flags = (long) args[0];
                installedPackages = BlackBoxCore.getBPackageManager().getInstalledPackages(Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[0];
                installedPackages = BlackBoxCore.getBPackageManager().getInstalledPackages(flags, BActivityThread.getUserId());
            }
            return ParceledListSliceCompat.create(installedPackages);
        }
    }

    @ProxyMethod("getApplicationInfo")
    public static class GetApplicationInfo extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String packageName = (String) args[0];
            ApplicationInfo applicationInfo;

            if (BuildCompat.isT()) {
                long flags = (long) args[1];
                applicationInfo = BlackBoxCore.getBPackageManager().getApplicationInfo(packageName, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[1];
                applicationInfo = BlackBoxCore.getBPackageManager().getApplicationInfo(packageName, flags, BActivityThread.getUserId());
            }

            if (applicationInfo != null) {
                return applicationInfo;
            }

            if (AppSystemEnv.isOpenPackage(packageName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("queryContentProviders")
    public static class QueryContentProviders extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            List<ProviderInfo> providers;

            if (BuildCompat.isT()) {
                long flags = (long) args[2];

                providers = BlackBoxCore.getBPackageManager()
                        .queryContentProviders(BActivityThread.getAppProcessName(), BActivityThread.getBUid(), Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[2];

                providers = BlackBoxCore.getBPackageManager()
                        .queryContentProviders(BActivityThread.getAppProcessName(), BActivityThread.getBUid(), flags, BActivityThread.getUserId());
            }
            return ParceledListSliceCompat.create(providers);
        }
    }

    @ProxyMethod("queryIntentReceivers")
    public static class QueryBroadcastReceivers extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = MethodParameterUtils.getFirstParam(args, Intent.class);
            String type = MethodParameterUtils.getFirstParam(args, String.class);
            List<ResolveInfo> resolves;

            if (BuildCompat.isT()) {
                Long flags = MethodParameterUtils.getFirstParam(args, Long.class);
                resolves = BlackBoxCore.getBPackageManager().queryBroadcastReceivers(intent, Math.toIntExact(flags), type, BActivityThread.getUserId());
            } else {
                Integer flags = MethodParameterUtils.getFirstParam(args, Integer.class);
                resolves = BlackBoxCore.getBPackageManager().queryBroadcastReceivers(intent, flags, type, BActivityThread.getUserId());
            }

            Slog.d(TAG, "queryIntentReceivers: " + resolves);

            // http://androidxref.com/7.0.0_r1/xref/frameworks/base/core/java/android/app/ApplicationPackageManager.java#872
            if (BuildCompat.isN()) {
                return ParceledListSliceCompat.create(resolves);
            }

            // http://androidxref.com/6.0.1_r10/xref/frameworks/base/core/java/android/app/ApplicationPackageManager.java#699
            return resolves;
        }
    }

    @ProxyMethod("resolveContentProvider")
    public static class ResolveContentProvider extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String authority = (String) args[0];
            ProviderInfo providerInfo;

            if (BuildCompat.isT()) {
                long flags = (long) args[1];
                providerInfo = BlackBoxCore.getBPackageManager().resolveContentProvider(authority, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[1];
                providerInfo = BlackBoxCore.getBPackageManager().resolveContentProvider(authority, flags, BActivityThread.getUserId());
            }

            if (providerInfo == null) {
                return method.invoke(who, args);
            }
            return providerInfo;
        }
    }

    @ProxyMethod("getPackagesForUid")
    public static class GetPackagesForUid extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int uid = (Integer) args[0];
            if (uid == BlackBoxCore.getHostUid()) {
                args[0] = BActivityThread.getBUid();
                uid = (int) args[0];
            }

            String[] packagesForUid = BlackBoxCore.getBPackageManager().getPackagesForUid(uid);
            Slog.d(TAG, args[0] + " , " + BActivityThread.getAppProcessName() + " getPackagesForUid: " + Arrays.toString(packagesForUid));
            return packagesForUid;
        }
    }

    @ProxyMethod("getInstallerPackageName")
    public static class GetInstallerPackageName extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return "com.android.vending";
        }
    }

    @ProxyMethod("getSharedLibraries")
    public static class GetSharedLibraries extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String packageName = (String) args[0];
            BPackageSettings packageSettings = BPackageManagerService.get().getBPackageSetting(packageName);
            if (packageSettings != null) {
                ArrayList<String> packageLibraries = new ArrayList<>();
                if (packageSettings.pkg.usesLibraries != null) {
                    packageLibraries.addAll(packageSettings.pkg.usesLibraries);
                }

                if (packageSettings.pkg.usesOptionalLibraries != null) {
                    packageLibraries.addAll(packageSettings.pkg.usesOptionalLibraries);
                }
                return ParceledListSliceCompat.create(packageLibraries);
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getComponentEnabledSetting")
    public static class GetComponentEnabledSetting extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
        }
    }
}
