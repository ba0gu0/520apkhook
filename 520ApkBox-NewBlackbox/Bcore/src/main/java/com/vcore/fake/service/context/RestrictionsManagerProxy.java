package com.vcore.fake.service.context;

import android.content.Context;

import java.lang.reflect.Method;

import black.android.content.IRestrictionsManager;
import black.android.os.ServiceManager;
import com.vcore.BlackBoxCore;
import com.vcore.fake.hook.BinderInvocationStub;
import com.vcore.fake.hook.MethodHook;
import com.vcore.fake.hook.ProxyMethod;

public class RestrictionsManagerProxy extends BinderInvocationStub {
    public RestrictionsManagerProxy() {
        super(ServiceManager.getService.call(Context.RESTRICTIONS_SERVICE));
    }

    @Override
    protected Object getWho() {
        return IRestrictionsManager.Stub.asInterface.call(ServiceManager.getService.call(Context.RESTRICTIONS_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.RESTRICTIONS_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("getApplicationRestrictions")
    public static class GetApplicationRestrictions extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            args[0] = BlackBoxCore.getHostPkg();
            return method.invoke(who, args);
        }
    }
}
