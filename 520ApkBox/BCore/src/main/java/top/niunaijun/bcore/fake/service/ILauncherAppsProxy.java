package top.niunaijun.bcore.fake.service;

import android.content.Context;

import java.lang.reflect.Method;

import black.android.content.pm.ILauncherApps;
import black.android.os.ServiceManager;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;
import top.niunaijun.bcore.utils.MethodParameterUtils;

public class ILauncherAppsProxy extends BinderInvocationStub {
    public ILauncherAppsProxy() {
        super(ServiceManager.getService.call(Context.LAUNCHER_APPS_SERVICE));
    }

    @Override
    protected Object getWho() {
        return ILauncherApps.Stub.asInterface.call(ServiceManager.getService.call(Context.LAUNCHER_APPS_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.LAUNCHER_APPS_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceFirstAppPkg(args);
        // TODO: shouldHideFromSuggestions
        return super.invoke(proxy, method, args);
    }
}
