package com.vcore.fake.service;

import black.android.os.ServiceManager;
import black.android.service.persistentdata.IPersistentDataBlockService;
import com.vcore.fake.hook.BinderInvocationStub;
import com.vcore.fake.service.base.ValueMethodProxy;

public class IPersistentDataBlockServiceProxy extends BinderInvocationStub {

    public IPersistentDataBlockServiceProxy() {
        super(ServiceManager.getService.call("persistent_data_block"));
    }

    @Override
    protected Object getWho() {
        return IPersistentDataBlockService.Stub.asInterface.call(ServiceManager.getService.call("persistent_data_block"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("persistent_data_block");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ValueMethodProxy("write", -1));
        addMethodHook(new ValueMethodProxy("read", new byte[0]));
        addMethodHook(new ValueMethodProxy("wipe", null));
        addMethodHook(new ValueMethodProxy("getDataBlockSize", 0));
        addMethodHook(new ValueMethodProxy("getMaximumDataBlockSize", 0));
        addMethodHook(new ValueMethodProxy("setOemUnlockEnabled", 0));
        addMethodHook(new ValueMethodProxy("getOemUnlockEnabled", false));
    }
}
