package com.vcore.fake.service;

import java.lang.reflect.Method;

import black.android.os.ServiceManager;
import black.android.view.IGraphicsStats;
import com.vcore.fake.hook.BinderInvocationStub;
import com.vcore.fake.hook.MethodHook;
import com.vcore.fake.hook.ProxyMethod;
import com.vcore.utils.MethodParameterUtils;

public class IGraphicsStatsProxy extends BinderInvocationStub {
    public IGraphicsStatsProxy() {
        super(ServiceManager.getService.call("graphicsstats"));
    }

    @Override
    protected Object getWho() {
        return IGraphicsStats.Stub.asInterface.call(ServiceManager.getService.call("graphicsstats"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("graphicsstats");

    }

    @Override
    public boolean isBadEnv() {
        return false;
    }


    @ProxyMethod("requestBufferForProcess")
    public static class RequestBufferForProcess extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }
}
