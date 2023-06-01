package top.niunaijun.bcore.fake.service;

import static android.content.pm.PackageManager.GET_META_DATA;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

import java.io.File;
import java.lang.reflect.Method;

import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.app.BActivityThread;
import top.niunaijun.bcore.fake.hook.MethodHook;
import top.niunaijun.bcore.fake.hook.ProxyMethod;
import top.niunaijun.bcore.fake.provider.FileProviderHandler;
import top.niunaijun.bcore.utils.ComponentUtils;
import top.niunaijun.bcore.utils.MethodParameterUtils;
import top.niunaijun.bcore.utils.Slog;
import top.niunaijun.bcore.utils.compat.BuildCompat;

public class ActivityManagerCommonProxy {
    public static final String TAG = "ActivityManagerCommonProxy";

    @ProxyMethod("startActivity")
    public static class StartActivity extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            Intent intent = getIntent(args);

            Slog.d(TAG, "Hook in : " + intent);
            assert intent != null;
            if (intent.getParcelableExtra("_B_|_target_") != null) {
                return method.invoke(who, args);
            }

            if (ComponentUtils.isRequestInstall(intent)) {
                File file = FileProviderHandler.convertFile(BActivityThread.getApplication(), intent.getData());
                if (BlackBoxCore.get().requestInstallPackage(file, BActivityThread.getUserId())) {
                    return 0;
                }

                intent.setData(FileProviderHandler.convertFileUri(BActivityThread.getApplication(), intent.getData()));
                return method.invoke(who, args);
            }

            String dataString = intent.getDataString();
            if (dataString != null && dataString.equals("package:" + BActivityThread.getAppPackageName())) {
                intent.setData(Uri.parse("package:" + BlackBoxCore.getHostPkg()));
            }

            ResolveInfo resolveInfo = BlackBoxCore.getBPackageManager().resolveActivity(intent, GET_META_DATA, getResolvedType(args),
                    BActivityThread.getUserId());
            if (resolveInfo == null) {
                String origPackage = intent.getPackage();
                if (intent.getPackage() == null && intent.getComponent() == null) {
                    intent.setPackage(BActivityThread.getAppPackageName());
                } else {
                    origPackage = intent.getPackage();
                }

                resolveInfo = BlackBoxCore.getBPackageManager().resolveActivity(intent, GET_META_DATA, getResolvedType(args),
                        BActivityThread.getUserId());
                if (resolveInfo == null) {
                    intent.setPackage(origPackage);
                    return method.invoke(who, args);
                }
            }

            intent.setExtrasClassLoader(who.getClass().getClassLoader());
            intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
            BlackBoxCore.getBActivityManager().startActivityAms(BActivityThread.getUserId(), getIntent(args),
                    getResolvedType(args), getResultTo(args), getResultWho(args),
                    getRequestCode(args), getFlags(args), getOptions(args));
            return 0;
        }

        private Intent getIntent(Object[] args) {
            int index;
            if (BuildCompat.isR()) {
                index = 3;
            } else {
                index = 2;
            }

            if (args[index] instanceof Intent) {
                return (Intent) args[index];
            }

            for (Object arg : args) {
                if (arg instanceof Intent) {
                    return (Intent) arg;
                }
            }
            return null;
        }

        private String getResolvedType(Object[] args) {
            int index;
            if (BuildCompat.isR()) {
                index = 4;
            } else {
                index = 3;
            }

            if (args[index] instanceof String) {
                return (String) args[index];
            }

            for (Object arg : args) {
                if (arg instanceof String) {
                    return (String) arg;
                }
            }
            return null;
        }

        private IBinder getResultTo(Object[] args) {
            int index;
            if (BuildCompat.isR()) {
                index = 5;
            } else {
                index = 4;
            }

            if (args[index] instanceof IBinder) {
                return (IBinder) args[index];
            }

            for (Object arg : args) {
                if (arg instanceof IBinder) {
                    return (IBinder) arg;
                }
            }
            return null;
        }

        private String getResultWho(Object[] args) {
            int index;
            if (BuildCompat.isR()) {
                index = 6;
            } else {
                index = 5;
            }

            if (args[index] instanceof String) {
                return (String) args[index];
            }

            for (Object arg : args) {
                if (arg instanceof String) {
                    return (String) arg;
                }
            }
            return null;
        }

        private int getRequestCode(Object[] args) {
            int index;
            if (BuildCompat.isR()) {
                index = 7;
            } else {
                index = 6;
            }

            if (args[index] instanceof Integer) {
                return (Integer) args[index];
            }

            for (Object arg : args) {
                if (arg instanceof Integer) {
                    return (Integer) arg;
                }
            }
            return 0;
        }

        private int getFlags(Object[] args) {
            int index;
            if (BuildCompat.isR()) {
                index = 8;
            } else {
                index = 7;
            }

            if (args[index] instanceof Integer) {
                return (Integer) args[index];
            }

            for (Object arg : args) {
                if (arg instanceof Integer) {
                    return (Integer) arg;
                }
            }
            return 0;
        }

        private Bundle getOptions(Object[] args) {
            int index;
            if (BuildCompat.isR()) {
                index = 9;
            } else {
                index = 8;
            }

            if (args[index] instanceof Bundle) {
                return (Bundle) args[index];
            }

            for (Object arg : args) {
                if (arg instanceof Bundle) {
                    return (Bundle) arg;
                }
            }
            return null;
        }
    }

    @ProxyMethod("startActivities")
    public static class StartActivities extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int index = getIntents();
            Intent[] intents = (Intent[]) args[index++];
            String[] resolvedTypes = (String[]) args[index++];
            IBinder resultTo = (IBinder) args[index++];
            Bundle options = (Bundle) args[index];

            if (!ComponentUtils.isSelf(intents)) {
                return method.invoke(who, args);
            }

            for (Intent intent : intents) {
                intent.setExtrasClassLoader(who.getClass().getClassLoader());
            }
            return BlackBoxCore.getBActivityManager().startActivities(BActivityThread.getUserId(), intents, resolvedTypes, resultTo, options);
        }

        public int getIntents() {
            return 2;
        }
    }

    @ProxyMethod("activityResumed")
    public static class ActivityResumed extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BlackBoxCore.getBActivityManager().onActivityResumed((IBinder) args[0]);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("activityDestroyed")
    public static class ActivityDestroyed extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BlackBoxCore.getBActivityManager().onActivityDestroyed((IBinder) args[0]);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("finishActivity")
    public static class FinishActivity extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BlackBoxCore.getBActivityManager().onFinishActivity((IBinder) args[0]);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getAppTasks")
    public static class GetAppTasks extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getCallingPackage")
    public static class GetCallingPackage extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BlackBoxCore.getBActivityManager().getCallingPackage((IBinder) args[0], BActivityThread.getUserId());
        }
    }

    @ProxyMethod("getCallingActivity")
    public static class GetCallingActivity extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BlackBoxCore.getBActivityManager().getCallingActivity((IBinder) args[0], BActivityThread.getUserId());
        }
    }
}
