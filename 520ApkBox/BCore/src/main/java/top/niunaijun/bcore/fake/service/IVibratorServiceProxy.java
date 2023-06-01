package top.niunaijun.bcore.fake.service;

import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.Method;

import black.android.os.IVibratorManagerService;
import black.android.os.ServiceManager;
import black.com.android.internal.os.IVibratorService;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;
import top.niunaijun.bcore.utils.MethodParameterUtils;
import top.niunaijun.bcore.utils.compat.BuildCompat;

public class IVibratorServiceProxy extends BinderInvocationStub {
    private static final String NAME;

    static {
        if (BuildCompat.isS()) {
            NAME = "vibrator_manager";
        } else {
            NAME = Context.VIBRATOR_SERVICE;
        }
    }

    public IVibratorServiceProxy() {
        super(ServiceManager.getService.call(NAME));
    }

    @Override
    protected Object getWho() {
        IBinder service = ServiceManager.getService.call(NAME);
        if (BuildCompat.isS()) {
            return IVibratorManagerService.Stub.asInterface.call(service);
        }
        return IVibratorService.Stub.asInterface.call(service);
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(NAME);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceFirstUid(args);
        MethodParameterUtils.replaceFirstAppPkg(args);
        return super.invoke(proxy, method, args);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
