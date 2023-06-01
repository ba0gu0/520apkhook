package top.niunaijun.bcore.fake.delegate;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.annotation.RequiresApi;

import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.app.configuration.AppLifecycleCallback;

public class BaseInstrumentationDelegate extends Instrumentation {
    protected Instrumentation mBaseInstrumentation;

    @Override
    public void onCreate(Bundle arguments) {
        mBaseInstrumentation.onCreate(arguments);
    }

    @Override
    public void start() {
        mBaseInstrumentation.start();
    }

    @Override
    public void onStart() {
        mBaseInstrumentation.onStart();
    }

    @Override
    public boolean onException(Object obj, Throwable e) {
        return mBaseInstrumentation.onException(obj, e);
    }

    @Override
    public void sendStatus(int resultCode, Bundle results) {
        mBaseInstrumentation.sendStatus(resultCode, results);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void addResults(Bundle results) {
        mBaseInstrumentation.addResults(results);
    }

    @Override
    public void finish(int resultCode, Bundle results) {
        mBaseInstrumentation.finish(resultCode, results);
    }

    @Override
    public void setAutomaticPerformanceSnapshots() {
        mBaseInstrumentation.setAutomaticPerformanceSnapshots();
    }

    @Override
    public void startPerformanceSnapshot() {
        mBaseInstrumentation.startPerformanceSnapshot();
    }

    @Override
    public void endPerformanceSnapshot() {
        mBaseInstrumentation.endPerformanceSnapshot();
    }

    @Override
    public void onDestroy() {
        mBaseInstrumentation.onDestroy();
    }

    @Override
    public Context getContext() {
        return mBaseInstrumentation.getContext();
    }

    @Override
    public ComponentName getComponentName() {
        return mBaseInstrumentation.getComponentName();
    }

    @Override
    public Context getTargetContext() {
        return mBaseInstrumentation.getTargetContext();
    }

    @Override
    public boolean isProfiling() {
        return mBaseInstrumentation.isProfiling();
    }

    @Override
    public void startProfiling() {
        mBaseInstrumentation.startProfiling();
    }

    @Override
    public void stopProfiling() {
        mBaseInstrumentation.stopProfiling();
    }

    @Override
    public void setInTouchMode(boolean inTouch) {
        mBaseInstrumentation.setInTouchMode(inTouch);
    }

    @Override
    public void waitForIdle(Runnable recipient) {
        mBaseInstrumentation.waitForIdle(recipient);
    }

    @Override
    public void waitForIdleSync() {
        mBaseInstrumentation.waitForIdleSync();
    }

    @Override
    public void runOnMainSync(Runnable runner) {
        mBaseInstrumentation.runOnMainSync(runner);
    }

    @Override
    public Activity startActivitySync(Intent intent) {
        return mBaseInstrumentation.startActivitySync(intent);
    }

    @Override
    public void addMonitor(ActivityMonitor monitor) {
        mBaseInstrumentation.addMonitor(monitor);
    }

    @Override
    public ActivityMonitor addMonitor(IntentFilter filter, ActivityResult result, boolean block) {
        return mBaseInstrumentation.addMonitor(filter, result, block);
    }

    @Override
    public ActivityMonitor addMonitor(String cls, ActivityResult result, boolean block) {
        return mBaseInstrumentation.addMonitor(cls, result, block);
    }

    @Override
    public boolean checkMonitorHit(ActivityMonitor monitor, int minHits) {
        return mBaseInstrumentation.checkMonitorHit(monitor, minHits);
    }

    @Override
    public Activity waitForMonitor(ActivityMonitor monitor) {
        return mBaseInstrumentation.waitForMonitor(monitor);
    }

    @Override
    public Activity waitForMonitorWithTimeout(ActivityMonitor monitor, long timeOut) {
        return mBaseInstrumentation.waitForMonitorWithTimeout(monitor, timeOut);
    }

    @Override
    public void removeMonitor(ActivityMonitor monitor) {
        mBaseInstrumentation.removeMonitor(monitor);
    }

    @Override
    public boolean invokeMenuActionSync(Activity targetActivity, int id, int flag) {
        return mBaseInstrumentation.invokeMenuActionSync(targetActivity, id, flag);
    }

    @Override
    public boolean invokeContextMenuAction(Activity targetActivity, int id, int flag) {
        return mBaseInstrumentation.invokeContextMenuAction(targetActivity, id, flag);
    }

    @Override
    public void sendStringSync(String text) {
        mBaseInstrumentation.sendStringSync(text);
    }

    @Override
    public void sendKeySync(KeyEvent event) {
        mBaseInstrumentation.sendKeySync(event);
    }

    @Override
    public void sendKeyDownUpSync(int key) {
        mBaseInstrumentation.sendKeyDownUpSync(key);
    }

    @Override
    public void sendCharacterSync(int keyCode) {
        mBaseInstrumentation.sendCharacterSync(keyCode);
    }

    @Override
    public void sendPointerSync(MotionEvent event) {
        mBaseInstrumentation.sendPointerSync(event);
    }

    @Override
    public void sendTrackballEventSync(MotionEvent event) {
        mBaseInstrumentation.sendTrackballEventSync(event);
    }

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return mBaseInstrumentation.newApplication(cl, className, context);
    }

    @Override
    public void callApplicationOnCreate(Application app) {
        mBaseInstrumentation.callApplicationOnCreate(app);
    }

    @Override
    public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application, Intent intent, ActivityInfo info, CharSequence title, Activity parent, String id, Object lastNonConfigurationInstance) throws IllegalAccessException, InstantiationException {
        return mBaseInstrumentation.newActivity(clazz, context, token, application, intent, info, title, parent, id, lastNonConfigurationInstance);
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return mBaseInstrumentation.newActivity(cl, className, intent);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        mBaseInstrumentation.callActivityOnCreate(activity, icicle);
        for (AppLifecycleCallback appLifecycleCallback : BlackBoxCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.onActivityCreated(activity, icicle);
        }
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        mBaseInstrumentation.callActivityOnCreate(activity, icicle, persistentState);
        for (AppLifecycleCallback appLifecycleCallback : BlackBoxCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.onActivityCreated(activity, icicle);
        }
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        mBaseInstrumentation.callActivityOnDestroy(activity);
        for (AppLifecycleCallback appLifecycleCallback : BlackBoxCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.onActivityDestroyed(activity);
        }
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        mBaseInstrumentation.callActivityOnRestoreInstanceState(activity, savedInstanceState);
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState, PersistableBundle persistentState) {
        mBaseInstrumentation.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState);
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle) {
        mBaseInstrumentation.callActivityOnPostCreate(activity, icicle);
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        mBaseInstrumentation.callActivityOnPostCreate(activity, icicle, persistentState);
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        mBaseInstrumentation.callActivityOnNewIntent(activity, intent);
    }

    @Override
    public void callActivityOnStart(Activity activity) {
        mBaseInstrumentation.callActivityOnStart(activity);
        for (AppLifecycleCallback appLifecycleCallback : BlackBoxCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.onActivityStarted(activity);
        }
    }

    @Override
    public void callActivityOnRestart(Activity activity) {
        mBaseInstrumentation.callActivityOnRestart(activity);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        mBaseInstrumentation.callActivityOnResume(activity);
        for (AppLifecycleCallback appLifecycleCallback : BlackBoxCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.onActivityResumed(activity);
        }
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        mBaseInstrumentation.callActivityOnStop(activity);
        for (AppLifecycleCallback appLifecycleCallback : BlackBoxCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.onActivityStopped(activity);
        }
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
        mBaseInstrumentation.callActivityOnSaveInstanceState(activity, outState);
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState, PersistableBundle outPersistentState) {
        mBaseInstrumentation.callActivityOnSaveInstanceState(activity, outState, outPersistentState);
        for (AppLifecycleCallback appLifecycleCallback : BlackBoxCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.onActivitySaveInstanceState(activity, outState);
        }
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        mBaseInstrumentation.callActivityOnPause(activity);
        for (AppLifecycleCallback appLifecycleCallback : BlackBoxCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.onActivityPaused(activity);
        }
    }

    @Override
    public void callActivityOnUserLeaving(Activity activity) {
        mBaseInstrumentation.callActivityOnUserLeaving(activity);
    }

    @Override
    public void startAllocCounting() {
        mBaseInstrumentation.startAllocCounting();
    }

    @Override
    public void stopAllocCounting() {
        mBaseInstrumentation.stopAllocCounting();
    }

    @Override
    public Bundle getAllocCounts() {
        return mBaseInstrumentation.getAllocCounts();
    }

    @Override
    public Bundle getBinderCounts() {
        return mBaseInstrumentation.getBinderCounts();
    }

    @Override
    public UiAutomation getUiAutomation() {
        return mBaseInstrumentation.getUiAutomation();
    }
}
