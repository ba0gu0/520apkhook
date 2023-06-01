package top.niunaijun.bcore.fake.service;

import android.os.IInterface;

import java.lang.reflect.Method;

import black.android.hardware.display.DisplayManagerGlobal;
import top.niunaijun.bcore.fake.hook.ClassInvocationStub;
import top.niunaijun.bcore.fake.hook.MethodHook;
import top.niunaijun.bcore.fake.hook.ProxyMethod;
import top.niunaijun.bcore.utils.MethodParameterUtils;

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
