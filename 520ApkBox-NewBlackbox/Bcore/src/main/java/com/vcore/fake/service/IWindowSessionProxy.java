package com.vcore.fake.service;

import android.os.IInterface;
import android.view.WindowManager;

import java.lang.reflect.Method;

import com.vcore.BlackBoxCore;
import com.vcore.fake.hook.BinderInvocationStub;
import com.vcore.fake.hook.MethodHook;
import com.vcore.fake.hook.ProxyMethod;

public class IWindowSessionProxy extends BinderInvocationStub {
    public static final String TAG = "WindowSessionStub";
    private final IInterface mSession;

    public IWindowSessionProxy(IInterface session) {
        super(session.asBinder());
        this.mSession = session;
    }

    @Override
    protected Object getWho() {
        return mSession;
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) { }

    @Override
    public boolean isBadEnv() {
        return false;
    }


    @ProxyMethod("addToDisplay")
    public static class AddToDisplay extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            for (Object arg : args) {
                if (arg == null) {
                    continue;
                }

                if (arg instanceof WindowManager.LayoutParams) {
                    ((WindowManager.LayoutParams) arg).packageName = BlackBoxCore.getHostPkg();
                }
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("addToDisplayAsUser")
    public static class AddToDisplayAsUser extends AddToDisplay { }
}
