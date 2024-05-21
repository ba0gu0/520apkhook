package com.vcore.fake.service;

import android.content.ComponentName;
import android.content.Context;

import java.lang.reflect.Method;

import black.android.app.admin.IDevicePolicyManager;
import black.android.os.ServiceManager;
import com.vcore.fake.hook.BinderInvocationStub;
import com.vcore.fake.hook.MethodHook;
import com.vcore.fake.hook.ProxyMethod;
import com.vcore.utils.MethodParameterUtils;

public class IDevicePolicyManagerProxy extends BinderInvocationStub {
    public IDevicePolicyManagerProxy() {
        super(ServiceManager.getService.call(Context.DEVICE_POLICY_SERVICE));
    }

    @Override
    protected Object getWho() {
        return IDevicePolicyManager.Stub.asInterface.call(ServiceManager.getService.call(Context.DEVICE_POLICY_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.DEVICE_POLICY_SERVICE);

    }

    @Override
    public boolean isBadEnv() {
        return false;
    }


    @ProxyMethod("getStorageEncryptionStatus")
    public static class GetStorageEncryptionStatus extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getDeviceOwnerComponent")
    public static class GetDeviceOwnerComponent extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return new ComponentName("", "");
        }
    }

    @ProxyMethod("getDeviceOwnerName")
    public static class GetDeviceOwnerName extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return "BlackBox";
        }
    }

    @ProxyMethod("getProfileOwnerName")
    public static class GetProfileOwnerName extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return "BlackBox";
        }
    }

    @ProxyMethod("isDeviceProvisioned")
    public static class IsDeviceProvisioned extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return true;
        }
    }
}
