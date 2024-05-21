package com.vcore.fake.service;

import android.content.ComponentName;

import java.lang.reflect.Method;

import black.android.os.ServiceManager;
import black.android.view.IAutoFillManager;
import com.vcore.BlackBoxCore;
import com.vcore.app.BActivityThread;
import com.vcore.fake.hook.BinderInvocationStub;
import com.vcore.fake.hook.MethodHook;
import com.vcore.fake.hook.ProxyMethod;
import com.vcore.proxy.ProxyManifest;

public class IAutofillManagerProxy extends BinderInvocationStub {
    public static final String TAG = "AutofillManagerStub";

    public IAutofillManagerProxy() {
        super(ServiceManager.getService.call("autofill"));
    }

    @Override
    protected Object getWho() {
        return IAutoFillManager.Stub.asInterface.call(ServiceManager.getService.call("autofill"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("autofill");

    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        addMethodHook(new StartSession());
    }

    @ProxyMethod("startSession")
    public static class StartSession extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] == null) {
                        continue;
                    }

                    if (args[i] instanceof ComponentName) {
                        args[i] = new ComponentName(BlackBoxCore.getHostPkg(), ProxyManifest.getProxyActivity(BActivityThread.getAppPid()));
                    }
                }
            }
            return method.invoke(who, args);
        }
    }
}
