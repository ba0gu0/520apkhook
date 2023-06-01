package top.niunaijun.bcore.fake.service;

import java.lang.reflect.Method;
import java.util.List;

import black.com.android.internal.net.VpnConfig;
import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.app.BActivityThread;
import top.niunaijun.bcore.fake.hook.MethodHook;
import top.niunaijun.bcore.fake.hook.ProxyMethod;
import top.niunaijun.bcore.proxy.ProxyVpnService;
import top.niunaijun.bcore.utils.MethodParameterUtils;

public class VpnCommonProxy {
    @ProxyMethod("setVpnPackageAuthorization")
    public static class SetVpnPackageAuthorization extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("prepareVpn")
    public static class PrepareVpn extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("establishVpn")
    public static class EstablishVpn extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            VpnConfig.user.set(args[0], ProxyVpnService.class.getName());

            handlePackage(VpnConfig.allowedApplications.get());
            handlePackage(VpnConfig.disallowedApplications.get());
            return method.invoke(who, args);
        }

        private void handlePackage(List<String> applications) {
            if (applications == null) {
                return;
            }

            if (applications.contains(BActivityThread.getAppPackageName())) {
                applications.add(BlackBoxCore.getHostPkg());
            }
        }
    }
}
