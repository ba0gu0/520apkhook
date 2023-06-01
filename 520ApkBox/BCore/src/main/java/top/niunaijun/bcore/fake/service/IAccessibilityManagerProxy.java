package top.niunaijun.bcore.fake.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.lang.reflect.Method;

import black.android.os.ServiceManager;
import black.android.view.accessibility.IAccessibilityManager;
import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.core.system.user.BUserHandle;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;
import top.niunaijun.bcore.fake.hook.MethodHook;
import top.niunaijun.bcore.fake.hook.ProxyMethods;

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
