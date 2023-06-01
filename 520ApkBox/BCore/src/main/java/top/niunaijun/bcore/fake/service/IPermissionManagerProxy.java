package top.niunaijun.bcore.fake.service;

import android.content.pm.PackageManager;
import android.os.Build;

import java.lang.reflect.Method;

import black.android.app.ActivityThread;
import black.android.app.ApplicationPackageManager;
import black.android.app.ContextImpl;
import black.android.os.ServiceManager;
import black.android.permission.IPermissionManager;
import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;
import top.niunaijun.bcore.fake.hook.MethodHook;
import top.niunaijun.bcore.fake.service.base.PkgMethodProxy;
import top.niunaijun.bcore.fake.service.base.UidMethodProxy;
import top.niunaijun.bcore.utils.MethodParameterUtils;
import top.niunaijun.bcore.utils.compat.BuildCompat;

public class IPermissionManagerProxy extends BinderInvocationStub {
    public static final String TAG = "IPermissionManagerProxy";

    public IPermissionManagerProxy() {
        super(ServiceManager.getService.call("permissionmgr"));
    }

    @Override
    protected Object getWho() {
        return IPermissionManager.Stub.asInterface.call(ServiceManager.getService.call("permissionmgr"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("permissionmgr");
        ActivityThread.sPermissionManager.set(proxyInvocation);

        Object systemContext = ActivityThread.getSystemContext.call(BlackBoxCore.mainThread());
        PackageManager packageManager = ContextImpl.mPackageManager.get(systemContext);
        if (packageManager != null) {
            try {
                ApplicationPackageManager.mPermissionManager.set(packageManager, proxyInvocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new PkgMethodProxy("getPermissionInfo"));
        addMethodHook(new PkgMethodProxy("getPermissionFlags"));
        addMethodHook(new PkgMethodProxy("updatePermissionFlags"));
        addMethodHook(new PkgMethodProxy("grantRuntimePermission"));
        addMethodHook(new PkgMethodProxy("revokeRuntimePermission"));
        addMethodHook(new PkgMethodProxy("shouldShowRequestPermissionRationale"));
        addMethodHook(new PkgMethodProxy("isPermissionRevokedByPolicy"));
        addMethodHook(new PkgMethodProxy("startOneTimePermissionSession"));
        addMethodHook(new PkgMethodProxy("stopOneTimePermissionSession"));
        addMethodHook(new PkgMethodProxy("setAutoRevokeExempted"));
        addMethodHook(new PkgMethodProxy("isAutoRevokeExempted"));

        if (BuildCompat.isT()) {
            addMethodHook(new PkgMethodProxy("getAllowlistedRestrictedPermissions"));
            addMethodHook(new PkgMethodProxy("addAllowlistedRestrictedPermission"));
            addMethodHook(new PkgMethodProxy("removeAllowlistedRestrictedPermission"));
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.S) {
            addMethodHook(new PkgMethodProxy("revokePostNotificationPermissionWithoutKillForTest"));
        } else {
            addMethodHook(new PkgMethodProxy("checkPermission"));
            addMethodHook(new UidMethodProxy("checkUidPermission", 1));
            addMethodHook(new PkgMethodProxy("getWhitelistedRestrictedPermissions"));
            addMethodHook(new PkgMethodProxy("addWhitelistedRestrictedPermission"));
            addMethodHook(new PkgMethodProxy("removeWhitelistedRestrictedPermission"));
            addMethodHook(new PkgMethodProxy("setDefaultBrowser"));
            addMethodHook(new PkgMethodProxy("grantDefaultPermissionsToActiveLuiApp"));
            addMethodHook("checkDeviceIdentifierAccess", new MethodHook() {
                @Override
                protected Object hook(Object who, Method method, Object[] args) throws Throwable {
                    MethodParameterUtils.replaceFirstAppPkg(args);
                    MethodParameterUtils.replaceLastUid(args);
                    return method.invoke(who, args);
                }
            });
        }
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
