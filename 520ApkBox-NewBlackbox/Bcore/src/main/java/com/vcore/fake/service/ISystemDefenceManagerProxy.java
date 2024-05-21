package com.vcore.fake.service;

import android.os.IBinder;

import black.android.os.ServiceManager;
import black.oem.vivo.ISystemDefenceManager;
import com.vcore.fake.hook.BinderInvocationStub;
import com.vcore.fake.service.base.PkgMethodProxy;

/**
 * @author Findger
 * @function
 * @date :2023/10/8 20:30
 **/
public class ISystemDefenceManagerProxy extends BinderInvocationStub {
    public ISystemDefenceManagerProxy() {
        super(ServiceManager.getService.call("system_defence_service"));
    }

    @Override
    protected Object getWho() {
        return ISystemDefenceManager.Stub.asInterface.call(ServiceManager.getService.call("system_defence_service"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("system_defence_service");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        addMethodHook(new PkgMethodProxy("checkTransitionTimoutErrorDefence"));
        addMethodHook(new PkgMethodProxy("checkSkipKilledByRemoveTask"));
        addMethodHook(new PkgMethodProxy("checkSmallIconNULLPackage"));
        addMethodHook(new PkgMethodProxy("checkDelayUpdate"));
        addMethodHook(new PkgMethodProxy("onSetActivityResumed"));
        addMethodHook(new PkgMethodProxy("checkReinstallPacakge"));
        addMethodHook(new PkgMethodProxy("reportFgCrashData"));
    }
}
