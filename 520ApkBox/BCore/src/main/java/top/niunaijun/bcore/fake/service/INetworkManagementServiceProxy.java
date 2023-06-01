package top.niunaijun.bcore.fake.service;

import java.lang.reflect.Method;

import black.android.os.INetworkManagementService;
import black.android.os.ServiceManager;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;
import top.niunaijun.bcore.fake.hook.MethodHook;
import top.niunaijun.bcore.fake.hook.ProxyMethod;
import top.niunaijun.bcore.fake.service.base.UidMethodProxy;
import top.niunaijun.bcore.utils.MethodParameterUtils;

public class INetworkManagementServiceProxy extends BinderInvocationStub {

    public INetworkManagementServiceProxy() {
        super(ServiceManager.getService.call("network_management"));
    }

    @Override
    protected Object getWho() {
        return INetworkManagementService.Stub.asInterface.call(ServiceManager.getService.call("network_management"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("network_management");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new UidMethodProxy("setUidCleartextNetworkPolicy", 0));
        addMethodHook(new UidMethodProxy("setUidMeteredNetworkBlacklist", 0));
        addMethodHook(new UidMethodProxy("setUidMeteredNetworkWhitelist", 0));
    }

    @ProxyMethod("getNetworkStatsUidDetail")
    public static class GetNetworkStatsUidDetail extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstUid(args);
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }
}
