package com.vcore.fake.service.context;

import java.lang.reflect.Method;

import black.android.content.IContentService;
import black.android.os.ServiceManager;
import com.vcore.fake.hook.BinderInvocationStub;
import com.vcore.fake.hook.MethodHook;
import com.vcore.fake.hook.ProxyMethod;

public class ContentServiceProxy extends BinderInvocationStub {
    public ContentServiceProxy() {
        super(ServiceManager.getService.call("content"));
    }

    @Override
    protected Object getWho() {
        return IContentService.Stub.asInterface.call(ServiceManager.getService.call("content"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("content");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("registerContentObserver")
    public static class RegisterContentObserver extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    @ProxyMethod("notifyChange")
    public static class NotifyChange extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }
}
