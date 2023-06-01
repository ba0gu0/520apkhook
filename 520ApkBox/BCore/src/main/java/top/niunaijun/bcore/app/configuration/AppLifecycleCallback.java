package top.niunaijun.bcore.app.configuration;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

public class AppLifecycleCallback implements Application.ActivityLifecycleCallbacks {
    public void beforeCreateApplication(String packageName, String processName, Context context, int userId) { }

    public void beforeApplicationOnCreate(String packageName, String processName, Application application, int userId) { }

    public void afterApplicationOnCreate(String packageName, String processName, Application application, int userId) { }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) { }

    @Override
    public void onActivityStarted(Activity activity) { }

    @Override
    public void onActivityResumed(Activity activity) { }

    @Override
    public void onActivityPaused(Activity activity) { }

    @Override
    public void onActivityStopped(Activity activity) { }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }

    @Override
    public void onActivityDestroyed(Activity activity) { }
}
