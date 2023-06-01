package top.niunaijun.bcore.fake.service;

import android.content.Context;

import black.android.net.IConnectivityManager;
import black.android.os.ServiceManager;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;
import top.niunaijun.bcore.fake.service.base.ValueMethodProxy;

public class IConnectivityManagerProxy extends BinderInvocationStub {
    public static final String TAG = "IConnectivityManagerProxy";

    public IConnectivityManagerProxy() {
        super(ServiceManager.getService.call(Context.CONNECTIVITY_SERVICE));
    }

    @Override
    protected Object getWho() {
        return IConnectivityManager.Stub.asInterface.call(ServiceManager.getService.call(Context.CONNECTIVITY_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ValueMethodProxy("getAllNetworkInfo", null));
        addMethodHook(new ValueMethodProxy("getAllNetworks",null));
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
