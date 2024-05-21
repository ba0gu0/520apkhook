package com.vcore.fake.service;

import android.content.Context;

import java.lang.reflect.Method;
import java.util.ArrayList;

import black.android.content.pm.UserInfo;
import black.android.os.IUserManager;
import black.android.os.ServiceManager;
import com.vcore.BlackBoxCore;
import com.vcore.app.BActivityThread;
import com.vcore.fake.hook.BinderInvocationStub;
import com.vcore.fake.hook.MethodHook;
import com.vcore.fake.hook.ProxyMethod;
import com.vcore.fake.service.base.PkgMethodProxy;
import com.vcore.fake.service.base.ValueMethodProxy;

public class IUserManagerProxy extends BinderInvocationStub {
    public IUserManagerProxy() {
        super(ServiceManager.getService.call(Context.USER_SERVICE));
    }

    @Override
    protected Object getWho() {
        return IUserManager.Stub.asInterface.call(ServiceManager.getService.call(Context.USER_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.USER_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        addMethodHook(new ValueMethodProxy("getProfileParent",null));
        addMethodHook(new ValueMethodProxy("getUserIcon",null));
        addMethodHook(new ValueMethodProxy("getDefaultGuestRestrictions",null));
        addMethodHook(new ValueMethodProxy("setDefaultGuestRestrictions",null));
        addMethodHook(new ValueMethodProxy("removeRestrictions",null));
        addMethodHook(new ValueMethodProxy("createUser",null));
        addMethodHook(new ValueMethodProxy("createProfileForUser",null));
    }

    @ProxyMethod("getApplicationRestrictions")
    public static class GetApplicationRestrictions extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            args[0] = BlackBoxCore.getHostPkg();
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getProfileParent")
    public static class GetProfileParent extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return UserInfo._new.newInstance(BActivityThread.getUserId(), "BlackBox", UserInfo.FLAG_PRIMARY);
        }
    }

    @ProxyMethod("getUsers")
    public static class GetUsers extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return new ArrayList<>();
        }
    }
}
