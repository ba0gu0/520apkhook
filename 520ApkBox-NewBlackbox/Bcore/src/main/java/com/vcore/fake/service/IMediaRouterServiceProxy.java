package com.vcore.fake.service;

import android.content.Context;

import java.lang.reflect.Method;

import black.android.media.IMediaRouterService;
import black.android.os.ServiceManager;
import com.vcore.core.system.accounts.RegisteredServicesParser;
import com.vcore.fake.hook.BinderInvocationStub;
import com.vcore.fake.hook.MethodHook;
import com.vcore.fake.hook.ProxyMethod;
import com.vcore.utils.MethodParameterUtils;

public class IMediaRouterServiceProxy extends BinderInvocationStub {
    public IMediaRouterServiceProxy() {
        super(ServiceManager.getService.call(Context.MEDIA_ROUTER_SERVICE));
    }

    @Override
    protected Object getWho() {
        return IMediaRouterService.Stub.asInterface.call(ServiceManager.getService.call(Context.MEDIA_ROUTER_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.MEDIA_ROUTER_SERVICE);

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceFirstAppPkg(args);
        return super.invoke(proxy, method, args);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("registerClientAsUser")
    public static class registerClientAsUser extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("registerRouter2")
    public static class registerRouter2 extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return method.invoke(who, args);
        }
    }
}
