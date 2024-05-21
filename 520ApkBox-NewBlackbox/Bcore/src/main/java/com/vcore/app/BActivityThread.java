package com.vcore.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.app.Service;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.RemoteException;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.WebView;

import java.lang.reflect.Field;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import black.android.app.ActivityManagerNative;
import black.android.app.ActivityThread;
import black.android.app.ActivityThreadNMR1;
import black.android.app.ActivityThreadQ;
import black.android.app.ContextImpl;
import black.android.app.LoadedApk;
import black.android.graphics.Compatibility;
import black.android.security.net.config.NetworkSecurityConfigProvider;
import black.com.android.internal.content.ReferrerIntent;
import black.dalvik.system.VMRuntime;

import com.vcore.BlackBoxCore;
import com.vcore.app.configuration.AppLifecycleCallback;
import com.vcore.app.dispatcher.AppServiceDispatcher;
import com.vcore.core.CrashHandler;
import com.vcore.core.IBActivityThread;
import com.vcore.core.IOCore;
import com.vcore.core.NativeCore;
import com.vcore.core.env.VirtualRuntime;
import com.vcore.core.system.user.BUserHandle;
import com.vcore.entity.AppConfig;
import com.vcore.entity.am.ReceiverData;
import com.vcore.fake.delegate.AppInstrumentation;
import com.vcore.fake.delegate.ContentProviderDelegate;
import com.vcore.fake.hook.HookManager;
import com.vcore.fake.service.HCallbackProxy;
import com.vcore.utils.Slog;
import com.vcore.utils.compat.ActivityManagerCompat;
import com.vcore.utils.compat.BuildCompat;
import com.vcore.utils.compat.ContextCompat;
import com.vcore.utils.compat.StrictModeCompat;

public class BActivityThread extends IBActivityThread.Stub {
    public static final String TAG = "BActivityThread";

    private AppBindData mBoundApplication;
    private Application mInitialApplication;
    private AppConfig mAppConfig;
    private final List<ProviderInfo> mProviders = new ArrayList<>();
    private final Handler mH = BlackBoxCore.get().getHandler();
    private static final Object mConfigLock = new Object();

    public static boolean isThreadInit() {
        return true;
    }

    private static final class SBActivityThreadHolder {
        static final BActivityThread sBActivityThread = new BActivityThread();
    }

    public static BActivityThread currentActivityThread() {
        return SBActivityThreadHolder.sBActivityThread;
    }

    public static AppConfig getAppConfig() {
        synchronized (mConfigLock) {
            return currentActivityThread().mAppConfig;
        }
    }

    public static List<ProviderInfo> getProviders() {
        return currentActivityThread().mProviders;
    }

    public static String getAppProcessName() {
        if (getAppConfig() != null) {
            return getAppConfig().processName;
        } else if (currentActivityThread().mBoundApplication != null) {
            return currentActivityThread().mBoundApplication.processName;
        }
        return null;
    }

    public static String getAppPackageName() {
        if (getAppConfig() != null) {
            return getAppConfig().packageName;
        } else if (currentActivityThread().mInitialApplication != null) {
            return currentActivityThread().mInitialApplication.getPackageName();
        }
        return null;
    }

    public static Application getApplication() {
        return currentActivityThread().mInitialApplication;
    }

    public static int getAppPid() {
        return getAppConfig() == null ? -1 : getAppConfig().bPID;
    }

    public static int getBUid() {
        return getAppConfig() == null ? BUserHandle.AID_APP_START : getAppConfig().bUID;
    }

    public static int getBAppId() {
        return BUserHandle.getAppId(getBUid());
    }

    public static int getCallingBUid() {
        return getAppConfig() == null ? BlackBoxCore.getHostUid() : getAppConfig().callingBUid;
    }

    public static int getUid() {
        return getAppConfig() == null ? -1 : getAppConfig().uid;
    }

    public static int getUserId() {
        return getAppConfig() == null ? 0 : getAppConfig().userId;
    }

    public void initProcess(AppConfig appConfig) {
        synchronized (mConfigLock) {
            if (this.mAppConfig != null && !this.mAppConfig.packageName.equals(appConfig.packageName)) {
                // 该进程已被attach
                throw new RuntimeException("Reject init process: " + appConfig.processName + ", this process is: " + this.mAppConfig.processName);
            }

            this.mAppConfig = appConfig;
            IBinder iBinder = asBinder();
            try {
                iBinder.linkToDeath(new DeathRecipient() {
                    @Override
                    public void binderDied() {
                        synchronized (mConfigLock) {
                            try {
                                iBinder.linkToDeath(this, 0);
                            } catch (RemoteException ignored) { }
                            mAppConfig = null;
                        }
                    }
                }, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isInit() {
        return mBoundApplication != null;
    }

    public Service createService(ServiceInfo serviceInfo, IBinder token) {
        if (!BActivityThread.currentActivityThread().isInit()) {
            BActivityThread.currentActivityThread().bindApplication(serviceInfo.packageName, serviceInfo.processName);
        }

        ClassLoader classLoader = LoadedApk.getClassLoader.call(mBoundApplication.info);
        Service service;
        try {
            service = (Service) classLoader.loadClass(serviceInfo.name).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Slog.e(TAG, "Unable to instantiate service " + serviceInfo.name + ": " + e);
            return null;
        }

        try {
            Context context = BlackBoxCore.getContext().createPackageContext(serviceInfo.packageName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);

            ContextImpl.setOuterContext.call(context, service);
            black.android.app.Service.attach.call(service, context, BlackBoxCore.mainThread(), serviceInfo.name, token, mInitialApplication,
                    ActivityManagerNative.getDefault.call());

            ContextCompat.fix(context);
            service.onCreate();
            return service;
        } catch (Exception e) {
            throw new RuntimeException("Unable to create service " + serviceInfo.name + ": " + e, e);
        }
    }

    public JobService createJobService(ServiceInfo serviceInfo) {
        if (!BActivityThread.currentActivityThread().isInit()) {
            BActivityThread.currentActivityThread().bindApplication(serviceInfo.packageName, serviceInfo.processName);
        }

        ClassLoader classLoader = LoadedApk.getClassLoader.call(mBoundApplication.info);
        JobService service;
        try {
            service = (JobService) classLoader.loadClass(serviceInfo.name).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Slog.e(TAG, "Unable to create JobService " + serviceInfo.name + ": " + e);
            return null;
        }

        try {
            Context context = BlackBoxCore.getContext().createPackageContext(serviceInfo.packageName, Context.CONTEXT_INCLUDE_CODE |
                    Context.CONTEXT_IGNORE_SECURITY);

            ContextImpl.setOuterContext.call(context, service);
            black.android.app.Service.attach.call(service, context, BlackBoxCore.mainThread(), serviceInfo.name,
                    BActivityThread.currentActivityThread().getActivityThread(), mInitialApplication, ActivityManagerNative.getDefault.call());

            ContextCompat.fix(context);
            service.onCreate();
            service.onBind(null);
            return service;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to create JobService " + serviceInfo.name
                            + ": " + e, e);
        }
    }

    public void bindApplication(final String packageName, final String processName) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            final ConditionVariable conditionVariable = new ConditionVariable();

            BlackBoxCore.get().getHandler().post(() -> {
                handleBindApplication(packageName, processName);
                conditionVariable.open();
            });
            conditionVariable.block();
        } else {
            handleBindApplication(packageName, processName);
        }
    }

    @SuppressLint("NewApi")
    public synchronized void handleBindApplication(String packageName, String processName) {
        if (isInit())
            return;
        try {
            CrashHandler.create();
        } catch (Throwable ignored) { }

        PackageInfo packageInfo = BlackBoxCore.getBPackageManager().getPackageInfo(packageName, PackageManager.GET_PROVIDERS, BActivityThread.getUserId());
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        if (packageInfo.providers == null) {
            packageInfo.providers = new ProviderInfo[]{};
        }
        mProviders.addAll(Arrays.asList(packageInfo.providers));

        Object boundApplication = ActivityThread.mBoundApplication.get(BlackBoxCore.mainThread());
        Context packageContext = createPackageContext(applicationInfo);
        Object loadedApk = ContextImpl.mPackageInfo.get(packageContext);

        LoadedApk.mSecurityViolation.set(loadedApk, false);
        LoadedApk.mApplicationInfo.set(loadedApk, applicationInfo);

        int targetSdkVersion = applicationInfo.targetSdkVersion;
        if (targetSdkVersion < Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.ThreadPolicy newPolicy = new StrictMode.ThreadPolicy.Builder(StrictMode.getThreadPolicy()).permitNetwork().build();
            StrictMode.setThreadPolicy(newPolicy);
        }

        if (BuildCompat.isN()) {
            if (targetSdkVersion < Build.VERSION_CODES.N) {
                StrictModeCompat.disableDeathOnFileUriExposure();
            }
        }

        if (BuildCompat.isPie()) {
            WebView.setDataDirectorySuffix(getUserId() + ":" + packageName + ":" + processName);
        }

        VirtualRuntime.setupRuntime(processName, applicationInfo);
        VMRuntime.setTargetSdkVersion.call(VMRuntime.getRuntime.call(), applicationInfo.targetSdkVersion);
        if (BuildCompat.isS()) {
            Compatibility.setTargetSdkVersion.call(applicationInfo.targetSdkVersion);
        }

        NativeCore.init(Build.VERSION.SDK_INT);
        assert packageContext != null;
        IOCore.get().enableRedirect(packageContext);

        AppBindData bindData = new AppBindData();
        bindData.appInfo = applicationInfo;
        bindData.processName = processName;
        bindData.info = loadedApk;
        bindData.providers = mProviders;

        ActivityThread.AppBindData.instrumentationName.set(boundApplication, new ComponentName(bindData.appInfo.packageName, Instrumentation.class.getName()));
        ActivityThread.AppBindData.appInfo.set(boundApplication, bindData.appInfo);
        ActivityThread.AppBindData.info.set(boundApplication, bindData.info);
        ActivityThread.AppBindData.processName.set(boundApplication, bindData.processName);
        ActivityThread.AppBindData.providers.set(boundApplication, bindData.providers);
        mBoundApplication = bindData;

        // SSL适配
        Security.removeProvider("AndroidNSSP");
        NetworkSecurityConfigProvider.install.call(packageContext);
        Application application;
        try {
            onBeforeCreateApplication(packageName, processName, packageContext);
            application = LoadedApk.makeApplication.call(loadedApk, false, null);

            mInitialApplication = application;
            ActivityThread.mInitialApplication.set(BlackBoxCore.mainThread(), mInitialApplication);
            ContextCompat.fix((Context) ActivityThread.getSystemContext.call(BlackBoxCore.mainThread()));
            ContextCompat.fix(mInitialApplication);
            installProviders(mInitialApplication, bindData.processName, bindData.providers);
            //fix wechat
            if (Build.VERSION.SDK_INT >= 24 && "com.tencent.mm:recovery".equals(processName)) {
                try {
                    Field field = application.getClassLoader().loadClass("com.tencent.recovery.Recovery").getField("context");
                    field.setAccessible(true);
                    if (field.get(null) != null) {
                        return;
                    }
                    field.set(null, application.getBaseContext());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            onBeforeApplicationOnCreate(packageName, processName, application);
            AppInstrumentation.get().callApplicationOnCreate(application);
            onAfterApplicationOnCreate(packageName, processName, application);
            HookManager.get().checkEnv(HCallbackProxy.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to makeApplication", e);
        }
    }

    public static Context createPackageContext(ApplicationInfo info) {
        try {
            return BlackBoxCore.getContext().createPackageContext(info.packageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void installProviders(Context context, String processName, List<ProviderInfo> provider) {
        long origId = Binder.clearCallingIdentity();
        try {
            for (ProviderInfo providerInfo : provider) {
                try {
                    if (processName.equals(providerInfo.processName) || providerInfo.processName.equals(context.getPackageName()) || providerInfo.multiprocess) {
                        installProvider(BlackBoxCore.mainThread(), context, providerInfo, null);
                        Log.d(TAG, "providerInfo.authority: " + providerInfo.authority);
                    }
                } catch (Throwable ignored) { }
            }
        } finally {
            Binder.restoreCallingIdentity(origId);
            ContentProviderDelegate.init();
        }
    }

    public Object getPackageInfo() {
        return mBoundApplication.info;
    }

    public static void installProvider(Object mainThread, Context context, ProviderInfo providerInfo, Object holder) {
        ActivityThread.installProvider.call(mainThread, context, holder, providerInfo, false, true, true);
    }

    /*public void loadXposed(Context context) {
        String vPackageName = getAppPackageName();
        String vProcessName = getAppProcessName();

        if (!TextUtils.isEmpty(vPackageName) && !TextUtils.isEmpty(vProcessName) && BXposedManager.get().isXPEnable()) {
            boolean isFirstApplication = vPackageName.equals(vProcessName);

            List<InstalledModule> installedModules = BXposedManager.get().getInstalledModules();
            for (InstalledModule installedModule : installedModules) {
                if (!installedModule.enable) {
                    continue;
                }

                try {
                    XposedInit.loadModule(installedModule.getApplication().sourceDir, context.getClassLoader());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            try {
                XposedInit.onPackageLoad(vPackageName, vProcessName, context.getApplicationInfo(), isFirstApplication, context.getClassLoader());
            } catch (Throwable ignored) { }
        }
        if (BlackBoxCore.get().isHideXposed()) {
            NativeCore.hideXposed();
        }
    }
*/
    @Override
    public IBinder getActivityThread() {
        return ActivityThread.getApplicationThread.call(BlackBoxCore.mainThread());
    }

    @Override
    public void bindApplication() {
        if (!isInit()) {
            bindApplication(getAppPackageName(), getAppProcessName());
        }
    }

    @Override
    public void stopService(Intent intent) {
        AppServiceDispatcher.get().stopService(intent);
    }

    @Override
    public void restartJobService(String selfId) { }

    @Override
    public IBinder acquireContentProviderClient(ProviderInfo providerInfo) {
        if (!isInit()) {
            bindApplication(BActivityThread.getAppConfig().packageName, BActivityThread.getAppConfig().processName);
        }

        String[] split = providerInfo.authority.split(";");
        for (String auth : split) {
            ContentProviderClient contentProviderClient = BlackBoxCore.getContext().getContentResolver().acquireContentProviderClient(auth);
            IInterface iInterface = black.android.content.ContentProviderClient.mContentProvider.get(contentProviderClient);

            if (iInterface == null) {
                continue;
            }
            return iInterface.asBinder();
        }
        return null;
    }

    @Override
    public IBinder peekService(Intent intent) {
        return AppServiceDispatcher.get().peekService(intent);
    }

    @Override
    public void finishActivity(final IBinder token) {
        mH.post(() -> {
            Map<IBinder, Object> activities = ActivityThread.mActivities.get(BlackBoxCore.mainThread());
            if (activities.isEmpty()) {
                return;
            }

            Object clientRecord = activities.get(token);
            if (clientRecord == null) {
                return;
            }

            Activity activity = getActivityByToken(token);
            while (activity.getParent() != null) {
                activity = activity.getParent();
            }

            int resultCode = black.android.app.Activity.mResultCode.get(activity);
            Intent resultData = black.android.app.Activity.mResultData.get(activity);
            ActivityManagerCompat.finishActivity(token, resultCode, resultData);
            black.android.app.Activity.mFinished.set(activity, true);
        });
    }

    @Override
    public void handleNewIntent(final IBinder token, final Intent intent) {
        mH.post(() -> {
            Intent newIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                newIntent = ReferrerIntent._new.newInstance(BlackBoxCore.getHostPkg());
            } else {
                newIntent = intent;
            }

            Object mainThread = BlackBoxCore.mainThread();
            if (ActivityThread.performNewIntents != null) {
                ActivityThread.performNewIntents.call(mainThread, token, Collections.singletonList(newIntent));
            } else if (ActivityThreadNMR1.performNewIntents != null) {
                ActivityThreadNMR1.performNewIntents.call(mainThread, token, Collections.singletonList(newIntent), true);
            } else if (ActivityThreadQ.handleNewIntent != null) {
                ActivityThreadQ.handleNewIntent.call(mainThread, token, Collections.singletonList(newIntent));
            }
        });
    }

    @Override
    public void scheduleReceiver(ReceiverData data) {
        if (!isInit()) {
            bindApplication();
        }
        mH.post(() -> {
            BroadcastReceiver mReceiver = null;
            Intent intent = data.intent;
            ActivityInfo activityInfo = data.activityInfo;
            BroadcastReceiver.PendingResult pendingResult = data.data.build();

            try {
                Context baseContext = mInitialApplication.getBaseContext();
                ClassLoader classLoader = baseContext.getClassLoader();
                intent.setExtrasClassLoader(classLoader);

                mReceiver = (BroadcastReceiver) classLoader.loadClass(activityInfo.name).newInstance();
                black.android.content.BroadcastReceiver.setPendingResult.call(mReceiver, pendingResult);
                mReceiver.onReceive(baseContext, intent);

                BroadcastReceiver.PendingResult finish = black.android.content.BroadcastReceiver.getPendingResult.call(mReceiver);
                if (finish != null) {
                    finish.finish();
                }
                BlackBoxCore.getBActivityManager().finishBroadcast(data.data);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                Slog.e(TAG, "Error receiving broadcast " + intent + " in " + mReceiver);
            }
        });
    }

    public static Activity getActivityByToken(IBinder token) {
        Map<IBinder, Object> iBinderObjectMap = ActivityThread.mActivities.get(BlackBoxCore.mainThread());
        return ActivityThread.ActivityClientRecord.activity.get(iBinderObjectMap.get(token));
    }

    private void onBeforeCreateApplication(String packageName, String processName, Context context) {
        for (AppLifecycleCallback appLifecycleCallback : BlackBoxCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.beforeCreateApplication(packageName, processName, context, BActivityThread.getUserId());
        }
    }

    private void onBeforeApplicationOnCreate(String packageName, String processName, Application application) {
        for (AppLifecycleCallback appLifecycleCallback : BlackBoxCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.beforeApplicationOnCreate(packageName, processName, application, BActivityThread.getUserId());
        }
    }

    private void onAfterApplicationOnCreate(String packageName, String processName, Application application) {
        for (AppLifecycleCallback appLifecycleCallback : BlackBoxCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.afterApplicationOnCreate(packageName, processName, application, BActivityThread.getUserId());
        }
    }

    public static class AppBindData {
        String processName;
        ApplicationInfo appInfo;
        List<ProviderInfo> providers;
        Object info;
    }
}
