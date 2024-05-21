package com.vcore.fake.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.IBinder;

import java.lang.reflect.Method;

import black.android.app.ISearchManager;
import black.android.os.ServiceManager;
import black.android.role.IRoleManager;
import com.vcore.BlackBoxCore;
import com.vcore.fake.hook.BinderInvocationStub;
import com.vcore.fake.hook.MethodHook;
import com.vcore.fake.service.base.PkgMethodProxy;

/**
 * @author Findger
 * @function
 * @date :2023/10/8 19:56
 **/
public class ISearchManagerProxy extends BinderInvocationStub {

    public ISearchManagerProxy() {
        super(ServiceManager.getService.call(Context.SEARCH_SERVICE));
    }

    @Override
    protected Object getWho() {
        return ISearchManager.Stub.asInterface.call(ServiceManager.getService.call(Context.SEARCH_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("search");
    }

    @Override
    protected void onBindMethod() {
        addMethodHook(new PkgMethodProxy("launchLegacyAssist"));
    }

    private static class GetSearchableInfo extends MethodHook{

        @Override
        protected String getMethodName() {
            return "getSearchableInfo";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName component = (ComponentName) args[0];
            if (component != null) {
                ActivityInfo activityInfo = BlackBoxCore.getPackageManager().getActivityInfo(component, 0);
                if (activityInfo != null) {
                    return null;
                }
            }
            return method.invoke(who,args);
        }
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
