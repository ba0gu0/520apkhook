package top.niunaijun.bcore.fake.service;

import black.android.net.wifi.IWifiManager;
import black.android.os.ServiceManager;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;

public class IWifiScannerProxy extends BinderInvocationStub {
    public IWifiScannerProxy() {
        super(ServiceManager.getService.call("wifiscanner"));
    }

    @Override
    protected Object getWho() {
        return IWifiManager.Stub.asInterface.call(ServiceManager.getService.call("wifiscanner"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("wifiscanner");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
