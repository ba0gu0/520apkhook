package com.vcore.fake.service.context.providers;

import android.os.Build;
import android.os.Bundle;
import android.os.IInterface;

import java.lang.reflect.Method;

import black.android.content.AttributionSource;
import com.vcore.app.BActivityThread;
import com.vcore.fake.hook.ClassInvocationStub;
import com.vcore.utils.compat.ContextCompat;

public class ContentProviderStub extends ClassInvocationStub implements BContentProvider {
    public static final String TAG = "ContentProviderStub";
    private IInterface mBase;
    private String mAppPkg;

    public IInterface wrapper(final IInterface contentProviderProxy, final String appPkg) {
        mBase = contentProviderProxy;
        mAppPkg = appPkg;

        injectHook();
        return (IInterface) getProxyInvocation();
    }

    private Bundle wrapBundle(String name, String value) {
        Bundle bundle = new Bundle();
        if (Build.VERSION.SDK_INT >= 24) {
            bundle.putString("name", name);
            bundle.putString("value", value);
        } else {
            bundle.putString(name, value);
        }
        return bundle;
    }

    @Override
    protected Object getWho() {
        return mBase;
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) { }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("asBinder".equals(method.getName())) {
            return method.invoke(mBase, args);
        }
        if (args != null && args.length > 0) {
            Object arg = args[0];
            if (arg instanceof String) {
                args[0] = mAppPkg;
                if ("android_id".equals(arg)) {
                    return wrapBundle("android_id", "");
                }
            } else if (arg.getClass().getName().equals(AttributionSource.REF.getClazz().getName())) {
                ContextCompat.fixAttributionSourceState(arg, BActivityThread.getBUid());
            }
        }
        return method.invoke(mBase, args);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
