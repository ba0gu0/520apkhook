package com.vcore.fake.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.lang.reflect.Method;

import black.android.os.ServiceManager;
import black.android.view.accessibility.IAccessibilityManager;
import com.vcore.BlackBoxCore;
import com.vcore.core.system.user.BUserHandle;
import com.vcore.fake.hook.BinderInvocationStub;
import com.vcore.fake.hook.MethodHook;
import com.vcore.fake.hook.ProxyMethods;

public class IAccessibilityManagerProxy extends BinderInvocationStub {
    public IAccessibilityManagerProxy() {
        super(ServiceManager.getService.call(Context.ACCESSIBILITY_SERVICE));
    }

    @Override
    protected Object getWho() {
        return IAccessibilityManager.Stub.asInterface.call(ServiceManager.getService.call(Context.ACCESSIBILITY_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethods({"interrupt", "sendAccessibilityEvent", "addClient", "removeClient", "getInstalledAccessibilityServiceList",
            "getEnabledAccessibilityServiceList", "addAccessibilityInteractionConnection", "getWindowToken", "setSystemAudioCaptioningEnabled",
            "isSystemAudioCaptioningUiEnabled", "setSystemAudioCaptioningUiEnabled"})
    public static class ReplaceUserId extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null) {
                int index = args.length - 1;
                Object arg = args[index];

                if (arg instanceof Integer) {
                    ApplicationInfo applicationInfo = BlackBoxCore.getContext().getApplicationInfo();
                    args[index] = BUserHandle.getUserId(applicationInfo.uid);
                }
            }
            return method.invoke(who, args);
        }
    }
}
