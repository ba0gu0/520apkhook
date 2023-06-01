package top.niunaijun.bcore.fake.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import black.android.app.ActivityClient;
import black.android.app.ActivityManagerNative;
import black.android.app.ActivityThread;
import black.android.app.IActivityManager;
import black.android.app.servertransaction.ClientTransaction;
import black.android.app.servertransaction.LaunchActivityItem;
import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.app.BActivityThread;
import top.niunaijun.bcore.fake.hook.IInjectHook;
import top.niunaijun.bcore.proxy.ProxyManifest;
import top.niunaijun.bcore.proxy.record.ProxyActivityRecord;
import top.niunaijun.bcore.utils.Slog;
import top.niunaijun.bcore.utils.compat.BuildCompat;

public class HCallbackProxy implements IInjectHook, Handler.Callback {
    public static final String TAG = "HCallbackProxy";
    private Handler.Callback mOtherCallback;
    private final AtomicBoolean mBeing = new AtomicBoolean(false);

    private Handler.Callback getHCallback() {
        return black.android.os.Handler.mCallback.get(getH());
    }

    private Handler getH() {
        Object currentActivityThread = BlackBoxCore.mainThread();
        return ActivityThread.mH.get(currentActivityThread);
    }

    @Override
    public void injectHook() {
        mOtherCallback = getHCallback();
        if (mOtherCallback != null && (mOtherCallback == this || mOtherCallback.getClass().getName().equals(this.getClass().getName()))) {
            mOtherCallback = null;
        }
        black.android.os.Handler.mCallback.set(getH(), this);
    }

    @Override
    public boolean isBadEnv() {
        Handler.Callback hCallback = getHCallback();
        return hCallback != null && hCallback != this;
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (!mBeing.getAndSet(true)) {
            try {
                if (BuildCompat.isPie()) {
                    if (msg.what == ActivityThread.H.EXECUTE_TRANSACTION.get()) {
                        if (handleLaunchActivity(msg.obj)) {
                            getH().sendMessageAtFrontOfQueue(Message.obtain(msg));
                            return true;
                        }
                    }
                } else {
                    if (msg.what == ActivityThread.H.LAUNCH_ACTIVITY.get()) {
                        if (handleLaunchActivity(msg.obj)) {
                            getH().sendMessageAtFrontOfQueue(Message.obtain(msg));
                            return true;
                        }
                    }
                }

                if (msg.what == ActivityThread.H.CREATE_SERVICE.get()) {
                    return handleCreateService(msg.obj);
                }

                if (mOtherCallback != null) {
                    return mOtherCallback.handleMessage(msg);
                }
                return false;
            } finally {
                mBeing.set(false);
            }
        }
        return false;
    }

    private Object getLaunchActivityItem(Object clientTransaction) {
        List<Object> mActivityCallbacks = ClientTransaction.mActivityCallbacks.get(clientTransaction);

        for (Object obj : mActivityCallbacks) {
            if (LaunchActivityItem.REF.getClazz().getName().equals(obj.getClass().getCanonicalName())) {
                return obj;
            }
        }
        return null;
    }

    private boolean handleLaunchActivity(Object client) {
        Object r;
        if (BuildCompat.isPie()) {
            // ClientTransaction
            r = getLaunchActivityItem(client);
        } else {
            // ActivityClientRecord
            r = client;
        }

        if (r == null) {
            return false;
        }

        Intent intent;
        IBinder token;
        if (BuildCompat.isPie()) {
            intent = LaunchActivityItem.mIntent.get(r);
            token = ClientTransaction.mActivityToken.get(client);
        } else {
            intent = ActivityThread.ActivityClientRecord.intent.get(r);
            token = ActivityThread.ActivityClientRecord.token.get(r);
        }

        if (intent == null) {
            return false;
        }

        ProxyActivityRecord stubRecord = ProxyActivityRecord.create(intent);
        ActivityInfo activityInfo = stubRecord.mActivityInfo;
        if (activityInfo != null) {
            if (BActivityThread.getAppConfig() == null) {
                BlackBoxCore.getBActivityManager().restartProcess(activityInfo.packageName, activityInfo.processName, stubRecord.mUserId);

                Intent launchIntentForPackage = BlackBoxCore.getBPackageManager().getLaunchIntentForPackage(activityInfo.packageName, stubRecord.mUserId);
                intent.setExtrasClassLoader(this.getClass().getClassLoader());
                ProxyActivityRecord.saveStub(intent, launchIntentForPackage, stubRecord.mActivityInfo, stubRecord.mActivityToken, stubRecord.mUserId);
                if (BuildCompat.isPie()) {
                    LaunchActivityItem.mIntent.set(r, intent);
                    LaunchActivityItem.mInfo.set(r, activityInfo);
                } else {
                    ActivityThread.ActivityClientRecord.intent.set(r, intent);
                    ActivityThread.ActivityClientRecord.activityInfo.set(r, activityInfo);
                }
                return true;
            }

            if (!BActivityThread.currentActivityThread().isInit()) {
                BActivityThread.currentActivityThread().bindApplication(activityInfo.packageName, activityInfo.processName);
                return true;
            }

            int taskId = IActivityManager.getTaskForActivity.call(ActivityManagerNative.getDefault.call(), token, false);
            BlackBoxCore.getBActivityManager().onActivityCreated(taskId, token, stubRecord.mActivityToken);

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.S || (Build.VERSION.SDK_INT == Build.VERSION_CODES.R && Build.VERSION.PREVIEW_SDK_INT == 1)) {
                Object record = ActivityThread.getLaunchingActivity.call(BlackBoxCore.mainThread(), token);

                ActivityThread.ActivityClientRecord.intent.set(record, stubRecord.mTarget);
                ActivityThread.ActivityClientRecord.activityInfo.set(record, activityInfo);
                ActivityThread.ActivityClientRecord.packageInfo.set(record, BActivityThread.currentActivityThread().getPackageInfo());

                checkActivityClient();
            } else if (BuildCompat.isPie()) {
                LaunchActivityItem.mIntent.set(r, stubRecord.mTarget);
                LaunchActivityItem.mInfo.set(r, activityInfo);
            } else {
                ActivityThread.ActivityClientRecord.intent.set(r, stubRecord.mTarget);
                ActivityThread.ActivityClientRecord.activityInfo.set(r, activityInfo);
            }
        }
        return false;
    }

    private boolean handleCreateService(Object data) {
        if (BActivityThread.getAppConfig() != null) {
            String appPackageName = BActivityThread.getAppPackageName();
            assert appPackageName != null;

            ServiceInfo serviceInfo = ActivityThread.CreateServiceData.info.get(data);
            if (!serviceInfo.name.equals(ProxyManifest.getProxyService(BActivityThread.getAppPid()))
                    && !serviceInfo.name.equals(ProxyManifest.getProxyJobService(BActivityThread.getAppPid()))) {
                Slog.d(TAG, "handleCreateService: " + data);
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(appPackageName, serviceInfo.name));
                BlackBoxCore.getBActivityManager().startService(intent, null, false, BActivityThread.getUserId());
                return true;
            }
        }
        return false;
    }

    private void checkActivityClient() {
        try {
            Object activityClientController = ActivityClient.getActivityClientController.call();
            if (!(activityClientController instanceof Proxy)) {
                IActivityClientProxy iActivityClientProxy = new IActivityClientProxy(activityClientController);
                iActivityClientProxy.onlyProxy(true);
                iActivityClientProxy.injectHook();

                Object instance = ActivityClient.getInstance.call();
                Object o = ActivityClient.INTERFACE_SINGLETON.get(instance);
                ActivityClient.ActivityClientControllerSingleton.mKnownInstance.set(o, iActivityClientProxy.getProxyInvocation());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
