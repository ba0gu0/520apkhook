package com.vcore.fake.service;

import black.android.net.IVpnManager;
import black.android.os.ServiceManager;
import com.vcore.fake.hook.BinderInvocationStub;
import com.vcore.fake.hook.ScanClass;

@ScanClass(VpnCommonProxy.class)
public class IVpnManagerProxy extends BinderInvocationStub {
    public static final String TAG = "IVpnManagerProxy";

    public IVpnManagerProxy() {
        super(ServiceManager.getService.call("vpn_management"));
    }

    @Override
    protected Object getWho() {
        return IVpnManager.Stub.asInterface.call(ServiceManager.getService.call("vpn_management"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("vpn_management");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
