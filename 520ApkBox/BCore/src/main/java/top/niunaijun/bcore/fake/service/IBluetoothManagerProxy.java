package top.niunaijun.bcore.fake.service;

import java.lang.reflect.Method;

import black.android.bluetooth.IBluetoothManager;
import black.android.os.ServiceManager;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;
import top.niunaijun.bcore.fake.hook.MethodHook;
import top.niunaijun.bcore.fake.hook.ProxyMethod;

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

    @ProxyMethod("getName")
    public static class GetName extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return null;
        }
    }
}
