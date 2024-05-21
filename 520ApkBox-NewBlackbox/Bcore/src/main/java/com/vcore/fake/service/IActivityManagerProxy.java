package com.vcore.fake.service;

import static android.content.pm.PackageManager.GET_META_DATA;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.app.ActivityManager;
import android.app.IServiceConnection;
import android.app.Notification;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;

import black.android.app.ActivityManagerNative;
import black.android.app.ActivityManagerOreo;
import black.android.app.IActivityManager;
import black.android.app.LoadedApk;
import black.android.content.ContentProviderNative;
import black.android.content.pm.UserInfo;
import black.android.util.Singleton;
import com.vcore.BlackBoxCore;
import com.vcore.app.BActivityThread;
import com.vcore.core.env.AppSystemEnv;
import com.vcore.core.system.DaemonService;
import com.vcore.entity.AppConfig;
import com.vcore.entity.am.RunningAppProcessInfo;
import com.vcore.entity.am.RunningServiceInfo;
import com.vcore.fake.delegate.ContentProviderDelegate;
import com.vcore.fake.delegate.InnerReceiverDelegate;
import com.vcore.fake.delegate.ServiceConnectionDelegate;
import com.vcore.fake.frameworks.BActivityManager;
import com.vcore.fake.frameworks.BPackageManager;
import com.vcore.fake.hook.ClassInvocationStub;
import com.vcore.fake.hook.MethodHook;
import com.vcore.fake.hook.ProxyMethod;
import com.vcore.fake.hook.ScanClass;
import com.vcore.fake.service.base.PkgMethodProxy;
import com.vcore.fake.service.context.providers.ContentProviderStub;
import com.vcore.proxy.ProxyManifest;
import com.vcore.proxy.record.ProxyBroadcastRecord;
import com.vcore.proxy.record.ProxyPendingRecord;
import com.vcore.utils.MethodParameterUtils;
import com.vcore.utils.Slog;
import com.vcore.utils.compat.ActivityManagerCompat;
import com.vcore.utils.compat.BuildCompat;
import com.vcore.utils.compat.ParceledListSliceCompat;
import com.vcore.utils.compat.TaskDescriptionCompat;

@ScanClass(ActivityManagerCommonProxy.class)
public class IActivityManagerProxy extends ClassInvocationStub {
    public static final String TAG = "IActivityManagerProxy";

    @Override
    protected Object getWho() {
        Object iActivityManager = null;
        if (BuildCompat.isOreo()) {
            iActivityManager = ActivityManagerOreo.IActivityManagerSingleton.get();
        } else if (BuildCompat.isL()) {
            iActivityManager = ActivityManagerNative.gDefault.get();
        }
        return Singleton.get.call(iActivityManager);
    }

    @Override
    protected void inject(Object base, Object proxy) {
        Object iActivityManager = null;
        if (BuildCompat.isOreo()) {
            iActivityManager = ActivityManagerOreo.IActivityManagerSingleton.get();
        } else if (BuildCompat.isL()) {
            iActivityManager = ActivityManagerNative.gDefault.get();
        }
        Singleton.mInstance.set(iActivityManager, proxy);
    }

    @Override
    public boolean isBadEnv() {
        return getProxyInvocation() != getWho();
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new PkgMethodProxy("getAppStartMode"));
        addMethodHook(new PkgMethodProxy("setAppLockedVerifying"));
        addMethodHook(new PkgMethodProxy("reportJunkFromApp"));
    }

    @ProxyMethod("getContentProvider")
    public static class GetContentProvider extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Exception {
            int authIndex = getAuthIndex();
            Object auth = args[authIndex];
            Object content;

            Slog.d(TAG, "Innovate getContentProvider: " + auth);
            if (auth instanceof String) {
                if (ProxyManifest.isProxy((String) auth)) {
                    Slog.d(TAG, "ProxyManifest.isProxy: " + auth);
                    return method.invoke(who, args);
                }

                if (BuildCompat.isQ()) {
                    args[1] = BlackBoxCore.getHostPkg();
                }

                if (auth.equals("settings") || auth.equals("media") || auth.equals("telephony")) {
                    content = method.invoke(who, args);
                    ContentProviderDelegate.update(content, (String) auth);
                    return content;
                } else {
                    Slog.d(TAG, "Hook getContentProvider: " + auth);
                    ProviderInfo providerInfo = BlackBoxCore.getBPackageManager().resolveContentProvider((String) auth, GET_META_DATA, BActivityThread.getUserId());

                    Slog.d(TAG, "Hook app: " + auth);
                    IBinder providerBinder = null;
                    if (BActivityThread.getAppPid() != -1 && providerInfo != null) {
                        AppConfig appConfig = BlackBoxCore.getBActivityManager().initProcess(providerInfo.packageName, providerInfo.processName, BActivityThread.getUserId());
                        if (appConfig.bPID != BActivityThread.getAppPid()) {
                            providerBinder = BlackBoxCore.getBActivityManager().acquireContentProviderClient(providerInfo);
                        }

                        args[authIndex] = ProxyManifest.getProxyAuthorities(appConfig.bPID);
                        args[getUserIndex()] = BlackBoxCore.getHostUserId();
                    }

                    if (providerBinder == null) {
                        return null;
                    }

                    content = method.invoke(who, args);
                    IActivityManager.ContentProviderHolder.info.set(content, providerInfo);
                    IActivityManager.ContentProviderHolder.provider.set(content, new ContentProviderStub().wrapper(ContentProviderNative.asInterface.call(providerBinder), BActivityThread.getAppPackageName()));
                }
                return content;
            }
            return method.invoke(who, args);
        }

        private int getAuthIndex() {
            // 10.0
            if (BuildCompat.isQ()) {
                return 2;
            } else {
                return 1;
            }
        }

        private int getUserIndex() {
            return getAuthIndex() + 1;
        }
    }

    @ProxyMethod("startService")
    public static class StartService extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[1];
            String resolvedType = (String) args[2];

            ResolveInfo resolveInfo = BlackBoxCore.getBPackageManager().resolveService(intent, 0, resolvedType, BActivityThread.getUserId());
            if (resolveInfo == null) {
                return method.invoke(who, args);
            }

            int requireForegroundIndex = getRequireForeground();
            boolean requireForeground = false;
            if (requireForegroundIndex != -1) {
                requireForeground = (boolean) args[requireForegroundIndex];
            }
            return BlackBoxCore.getBActivityManager().startService(intent, resolvedType, requireForeground, BActivityThread.getUserId());
        }

        public int getRequireForeground() {
            if (BuildCompat.isOreo()) {
                return 3;
            }
            return -1;
        }
    }

    @ProxyMethod("stopService")
    public static class StopService extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[1];
            String resolvedType = (String) args[2];
            return BlackBoxCore.getBActivityManager().stopService(intent, resolvedType, BActivityThread.getUserId());
        }
    }

    @ProxyMethod("stopServiceToken")
    public static class StopServiceToken extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            IBinder token = (IBinder) args[1];
            BlackBoxCore.getBActivityManager().stopServiceToken(componentName, token, BActivityThread.getUserId());
            return true;
        }
    }

    @ProxyMethod("bindService")
    public static class BindService extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[2];
            String resolvedType = (String) args[3];
            IServiceConnection connection = (IServiceConnection) args[4];
            int userId = intent.getIntExtra("_B_|_UserId", -1);
            userId = userId == -1 ? BActivityThread.getUserId() : userId;
            int callingPkgIdx = false ? 7 : (char) 6;
            if (Build.VERSION.SDK_INT >= 23 && args.length >= 8 && (args[callingPkgIdx] instanceof String)) {
                args[callingPkgIdx] = BlackBoxCore.getHostPkg();
            }
            long flags = getIntOrLongValue(args[5]);
            ResolveInfo resolveInfo = BlackBoxCore.getBPackageManager().resolveService(intent, 0, resolvedType, userId);
            if (resolveInfo != null || AppSystemEnv.isOpenPackage(intent.getComponent())) {
                if (BuildCompat.isU()){
                    args[5] = Long.valueOf(flags & 2147483647L);
                }else{
                    args[5] = Integer.valueOf((int) (flags & 2147483647L));
                }
                Intent proxyIntent = BlackBoxCore.getBActivityManager().bindService(intent, connection == null ? null : connection.asBinder(), resolvedType,
                        userId);
                if (connection != null) {
                    if (intent.getComponent() == null && resolveInfo != null) {
                        intent.setComponent(new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name));
                    }

                    IServiceConnection proxy = ServiceConnectionDelegate.createProxy(connection, intent);
                    args[4] = proxy;

                    WeakReference<?> weakReference = LoadedApk.ServiceDispatcher.InnerConnection.mDispatcher.get(connection);
                    if (weakReference != null) {
                        LoadedApk.ServiceDispatcher.mConnection.set(weakReference.get(), proxy);
                    }
                }
                if (BuildCompat.isT()){
                    if (proxyIntent != null) {
                        args[2] = proxyIntent;
                        return method.invoke(who, args);
                    }
                }else{
                    return method.invoke(who, args);
                }
            }
            return method.invoke(who, args);
        }

        @Override
        protected boolean isEnable() {
            return BlackBoxCore.get().isBlackProcess() || BlackBoxCore.get().isServerProcess();
        }
    }

    public static long getIntOrLongValue(Object obj) {
        if (obj == null) {
            return 0L;
        }
        if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        }
        if (obj instanceof Long) {
            return ((Long) obj).longValue();
        }
        return -1L;
    }

    // 10.0
    @ProxyMethod("bindIsolatedService")
    public static class BindIsolatedService extends BindService {

        @Override
        protected Object beforeHook(Object who, Method method, Object[] args) throws Throwable {
            // instanceName
            args[6] = null;
            return super.beforeHook(who, method, args);
        }
    }

    @ProxyMethod("unbindService")
    public static class UnbindService extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IServiceConnection iServiceConnection = (IServiceConnection) args[0];
            if (iServiceConnection == null) {
                return method.invoke(who, args);
            }

            BlackBoxCore.getBActivityManager().unbindService(iServiceConnection.asBinder(), BActivityThread.getUserId());
            ServiceConnectionDelegate delegate = ServiceConnectionDelegate.getDelegate(iServiceConnection.asBinder());
            if (delegate != null) {
                args[0] = delegate;
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getRunningAppProcesses")
    public static class GetRunningAppProcesses extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            RunningAppProcessInfo runningAppProcesses = BActivityManager.get().getRunningAppProcesses(BActivityThread.getAppPackageName(), BActivityThread.getUserId());
            if (runningAppProcesses == null) {
                return new ArrayList<>();
            }
            return runningAppProcesses.mAppProcessInfoList;
        }
    }

    @ProxyMethod("getServices")
    public static class GetServices extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            RunningServiceInfo runningServices = BActivityManager.get().getRunningServices(BActivityThread.getAppPackageName(), BActivityThread.getUserId());
            if (runningServices == null) {
                return new ArrayList<>();
            }
            return runningServices.mRunningServiceInfoList;
        }
    }

    @ProxyMethod("getIntentSender")
    public static class GetIntentSender extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int type = (int) args[0];
            Intent[] intents = (Intent[]) args[getIntentsIndex(args)];
            MethodParameterUtils.replaceFirstAppPkg(args);

            for (int i = 0; i < intents.length; i++) {
                Intent intent = intents[i];
                if (type == ActivityManagerCompat.INTENT_SENDER_ACTIVITY) {
                    Intent shadow = new Intent();
                    shadow.setComponent(new ComponentName(BlackBoxCore.getHostPkg(), ProxyManifest.getProxyPendingActivity(BActivityThread.getAppPid())));

                    ProxyPendingRecord.saveStub(shadow, intent, BActivityThread.getUserId());
                    intents[i] = shadow;
                }
            }

            IInterface invoke = (IInterface) method.invoke(who, args);
            if (invoke != null) {
                String[] packagesForUid = BPackageManager.get().getPackagesForUid(BActivityThread.getCallingBUid());
                if (packagesForUid.length < 1) {
                    packagesForUid = new String[]{BlackBoxCore.getHostPkg()};
                }
                BlackBoxCore.getBActivityManager().getIntentSender(invoke.asBinder(), packagesForUid[0], BActivityThread.getCallingBUid());
            }
            return invoke;
        }

        private int getIntentsIndex(Object[] args) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent[]) {
                    return i;
                }
            }
            if (BuildCompat.isR()) {
                return 6;
            } else {
                return 5;
            }
        }
    }

    @ProxyMethod("getPackageForIntentSender")
    public static class GetPackageForIntentSender extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IInterface invoke = (IInterface) args[0];
            return BlackBoxCore.getBActivityManager().getPackageForIntentSender(invoke.asBinder());
        }
    }

    @ProxyMethod("getUidForIntentSender")
    public static class getUidForIntentSender extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IInterface invoke = (IInterface) args[0];
            return BlackBoxCore.getBActivityManager().getUidForIntentSender(invoke.asBinder());
        }
    }

    @ProxyMethod("broadcastIntent")
    public static class BroadcastIntent extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int intentIndex = getIntentIndex(args);
            Intent intent = (Intent) args[intentIndex];
            String resolvedType = (String) args[intentIndex + 1];

            Intent proxyIntent = BlackBoxCore.getBActivityManager().sendBroadcast(intent, resolvedType, BActivityThread.getUserId());
            if (proxyIntent != null) {
                proxyIntent.setExtrasClassLoader(BActivityThread.getApplication().getClassLoader());

                ProxyBroadcastRecord.saveStub(proxyIntent, intent, BActivityThread.getUserId());
                args[intentIndex] = proxyIntent;
            }
            // ignore permission
            for (int i = 0; i < args.length; i++) {
                Object o = args[i];
                if (o instanceof String[]) {
                    args[i] = null;
                }
            }
            return method.invoke(who, args);
        }

        int getIntentIndex(Object[] args) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof Intent) {
                    return i;
                }
            }
            return 1;
        }
    }

    @ProxyMethod("peekService")
    public static class PeekService extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            Intent intent = (Intent) args[0];
            String resolvedType = (String) args[1];
            return BlackBoxCore.getBActivityManager().peekService(intent, resolvedType, BActivityThread.getUserId());
        }
    }

    // TODO
    @ProxyMethod("sendIntentSender")
    public static class SendIntentSender extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    @ProxyMethod("registerReceiver")
    public static class RegisterReceiver extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            int receiverIndex = getReceiverIndex();
            if (args[receiverIndex] != null) {
                IIntentReceiver intentReceiver = (IIntentReceiver) args[receiverIndex];
                IIntentReceiver proxy = InnerReceiverDelegate.createProxy(intentReceiver);

                WeakReference<?> weakReference = LoadedApk.ReceiverDispatcher.InnerReceiver.mDispatcher.get(intentReceiver);
                if (weakReference != null) {
                    LoadedApk.ReceiverDispatcher.mIIntentReceiver.set(weakReference.get(), proxy);
                }

                args[receiverIndex] = proxy;
            }
            // ignore permission
            if (args[getPermissionIndex()] != null) {
                args[getPermissionIndex()] = null;
            }
            return method.invoke(who, args);
        }

        public int getReceiverIndex() {
            if (BuildCompat.isS()) {
                return 4;
            } else if (BuildCompat.isR()) {
                return 3;
            }
            return 2;
        }

        public int getPermissionIndex() {
            if (BuildCompat.isS()) {
                return 6;
            } else if (BuildCompat.isR()) {
                return 5;
            }
            return 4;
        }
    }

    @ProxyMethod("grantUriPermission")
    public static class GrantUriPermission extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastUid(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setServiceForeground")
    public static class SetServiceForeground extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Notification notification = (Notification) args[3];

            Intent intent = new Intent(BlackBoxCore.getContext(), DaemonService.class);
            if (notification != null) {
                if (BuildCompat.isOreo()) {
                    BlackBoxCore.getContext().startForegroundService(intent);
                } else {
                    BlackBoxCore.getContext().startService(intent);
                }
            } else {
                BlackBoxCore.getContext().stopService(intent);
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getHistoricalProcessExitReasons")
    public static class GetHistoricalProcessExitReasons extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return ParceledListSliceCompat.create(new ArrayList<>());
        }
    }

    @ProxyMethod("getCurrentUser")
    public static class GetCurrentUser extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return UserInfo._new.newInstance(BActivityThread.getUserId(), "BlackBox", UserInfo.FLAG_PRIMARY.get());
        }
    }

    @ProxyMethod("checkPermission")
    public static class CheckPermission extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastUid(args);
            String permission = (String) args[0];

            if (permission.equals(Manifest.permission.ACCOUNT_MANAGER) || permission.equals(Manifest.permission.SEND_SMS)) {
                return PackageManager.PERMISSION_GRANTED;
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("checkUriPermission")
    public static class CheckUriPermission extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return PERMISSION_GRANTED;
        }
    }

    // for < Android 10
    @ProxyMethod("setTaskDescription")
    public static class SetTaskDescription extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ActivityManager.TaskDescription td = (ActivityManager.TaskDescription) args[1];
            args[1] = TaskDescriptionCompat.fix(td);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("overridePendingTransition")
    public static class OverridePendingTransition extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String packageName = (String) args[1];
            if ("com.tencent.mm".equals(packageName)) {
                return null;
            }else{
                return method.invoke(who, args);
            }
        }
    }

    @ProxyMethod("setPackageAskScreenCompat")
    public static class SetPackageAskScreenCompat extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                if (args.length > 0 && args[0] instanceof String) {
                    args[0] = BlackBoxCore.getHostPkg();
                }
            }
            return method.invoke(who,args);
        }
    }

    @ProxyMethod("handleIncomingUser")
    public static class HandleIncomingUser extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BlackBoxCore.get().isBlackProcess()){
                int lastIndex = args.length - 1;
                if (args[lastIndex] instanceof String) {
                    args[lastIndex] = BlackBoxCore.getHostPkg();
                }
                return method.invoke(who, args);
            }else{
                return method.invoke(who, args);
            }
        }
    }

    @ProxyMethod("getPersistedUriPermissions")
    public static class GetPersistedUriPermissions extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BlackBoxCore.get().isBlackProcess()){
                MethodParameterUtils.replaceFirstAppPkg(args);
                return method.invoke(who, args);
            }else{
                return method.invoke(who, args);
            }
        }
    }
}
