package top.niunaijun.bcore.fake.service;

import black.android.os.ServiceManager;
import black.android.view.IAutoFillManager;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;

public class ISystemUpdateProxy extends BinderInvocationStub {
    public ISystemUpdateProxy() {
        super(ServiceManager.getService.call("system_update"));
    }

    @Override
    protected Object getWho() {
        return IAutoFillManager.Stub.asInterface.call(ServiceManager.getService.call("system_update"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("system_update");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
