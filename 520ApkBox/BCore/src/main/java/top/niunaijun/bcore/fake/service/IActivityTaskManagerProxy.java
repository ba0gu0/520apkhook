package top.niunaijun.bcore.fake.service;

import android.app.ActivityManager;

import java.lang.reflect.Method;

import black.android.app.ActivityTaskManager;
import black.android.app.IActivityTaskManager;
import black.android.os.ServiceManager;
import black.android.util.Singleton;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;
import top.niunaijun.bcore.fake.hook.MethodHook;
import top.niunaijun.bcore.fake.hook.ProxyMethod;
import top.niunaijun.bcore.fake.hook.ScanClass;
import top.niunaijun.bcore.utils.compat.TaskDescriptionCompat;

@ScanClass(ActivityManagerCommonProxy.class)
public class IActivityTaskManagerProxy extends BinderInvocationStub {
    public static final String TAG = "ActivityTaskManager";

    public IActivityTaskManagerProxy() {
        super(ServiceManager.getService.call("activity_task"));
    }

    @Override
    protected Object getWho() {
        return IActivityTaskManager.Stub.asInterface.call(ServiceManager.getService.call("activity_task"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("activity_task");

        Object o = ActivityTaskManager.IActivityTaskManagerSingleton.get();
        Singleton.mInstance.set(o, IActivityTaskManager.Stub.asInterface.call(this));
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    // for >= Android 10 && < Android 12
    @ProxyMethod("setTaskDescription")
    public static class SetTaskDescription extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ActivityManager.TaskDescription td = (ActivityManager.TaskDescription) args[1];
            args[1] = TaskDescriptionCompat.fix(td);
            return method.invoke(who, args);
        }
    }
}
