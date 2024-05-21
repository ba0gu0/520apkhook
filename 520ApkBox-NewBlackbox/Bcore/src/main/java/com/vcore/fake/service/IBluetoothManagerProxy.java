package com.vcore.fake.service;

import java.lang.reflect.Method;

import black.android.bluetooth.IBluetoothManager;
import black.android.os.ServiceManager;
import com.vcore.fake.hook.BinderInvocationStub;
import com.vcore.fake.hook.MethodHook;
import com.vcore.fake.hook.ProxyMethod;

public class IBluetoothManagerProxy extends BinderInvocationStub {
    public static final String TAG = "IBluetoothManagerProxy";

    public IBluetoothManagerProxy() {
        super(ServiceManager.getService.call("bluetooth_manager"));
    }

    @Override
    protected Object getWho() {
        return IBluetoothManager.Stub.asInterface.call(ServiceManager.getService.call("bluetooth_manager"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("bluetooth_manager");

    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        addMethodHook(new GetName());
    }

    @ProxyMethod("getName")
    public static class GetName extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return null;
        }
    }
}
