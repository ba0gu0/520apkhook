package top.niunaijun.bcore.fake.service;

import android.content.Context;

import black.android.app.IAlarmManager;
import black.android.os.ServiceManager;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;
import top.niunaijun.bcore.fake.service.base.ValueMethodProxy;

public class IAlarmManagerProxy extends BinderInvocationStub {
    public IAlarmManagerProxy() {
        super(ServiceManager.getService.call(Context.ALARM_SERVICE));
    }

    @Override
    protected Object getWho() {
        return IAlarmManager.Stub.asInterface.call(ServiceManager.getService.call(Context.ALARM_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.ALARM_SERVICE);
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ValueMethodProxy("set", 0));
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
