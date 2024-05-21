package com.vcore.fake.service;

import android.os.IInterface;

import java.lang.reflect.Method;

import black.android.hardware.display.DisplayManagerGlobal;
import com.vcore.fake.hook.ClassInvocationStub;
import com.vcore.fake.hook.MethodHook;
import com.vcore.fake.hook.ProxyMethod;
import com.vcore.utils.MethodParameterUtils;

public class IDisplayManagerProxy extends ClassInvocationStub {
    public IDisplayManagerProxy() { }

    @Override
    protected Object getWho() {
        return DisplayManagerGlobal.mDm.get(DisplayManagerGlobal.getInstance.call());
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        Object dmg = DisplayManagerGlobal.getInstance.call();
        DisplayManagerGlobal.mDm.set(dmg, getProxyInvocation());

    }

    @Override
    protected void onBindMethod() {
        addMethodHook(new CreateVirtualDisplay());
    }

    @Override
    public boolean isBadEnv() {
        Object dmg = DisplayManagerGlobal.getInstance.call();
        IInterface mDm = DisplayManagerGlobal.mDm.get(dmg);
        return mDm != getProxyInvocation();
    }

    @ProxyMethod("createVirtualDisplay")
    public static class CreateVirtualDisplay extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }
}
