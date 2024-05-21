package com.vcore.fake.service;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

import black.android.os.ServiceManager;
import black.android.telephony.TelephonyManager;
import com.vcore.BlackBoxCore;
import com.vcore.fake.hook.BinderInvocationStub;
import com.vcore.fake.hook.MethodHook;
import com.vcore.fake.hook.ProxyMethod;
import com.vcore.fake.service.base.PkgMethodProxy;
import com.vcore.utils.Md5Utils;
import com.vcore.utils.MethodParameterUtils;
import com.vcore.utils.compat.BuildCompat;

public class IPhoneSubInfoProxy extends BinderInvocationStub {
    public static final String TAG = "IPhoneSubInfoProxy";

    public IPhoneSubInfoProxy() {
        super(ServiceManager.getService.call("iphonesubinfo"));
    }

    @Override
    protected Object getWho() {
        if (BuildCompat.isR()) {
            return TelephonyManager.sIPhoneSubInfo.get();
        }
        android.telephony.TelephonyManager telephonyManager = (android.telephony.TelephonyManager) BlackBoxCore.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        return TelephonyManager.getSubscriberInfo.call(telephonyManager);
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        if (BuildCompat.isR()) {
            TelephonyManager.sIPhoneSubInfo.set(proxyInvocation);
        }
        replaceSystemService("iphonesubinfo");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.e(TAG, "Test");
        MethodParameterUtils.replaceLastAppPkg(args);
        return super.invoke(proxy, method, args);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        addMethodHook(new PkgMethodProxy("getNaiForSubscriber"));
        addMethodHook(new PkgMethodProxy("getDeviceSvn"));
        addMethodHook(new PkgMethodProxy("getDeviceSvnUsingSubId"));
        addMethodHook(new PkgMethodProxy("getGroupIdLevel1"));
        addMethodHook(new PkgMethodProxy("getGroupIdLevel1ForSubscriber"));
        addMethodHook(new PkgMethodProxy("getLine1AlphaTag"));
        addMethodHook(new PkgMethodProxy("getLine1AlphaTagForSubscriber"));
        addMethodHook(new PkgMethodProxy("getMsisdn"));
        addMethodHook(new PkgMethodProxy("getMsisdnForSubscriber"));
        addMethodHook(new PkgMethodProxy("getVoiceMailNumber"));
        addMethodHook(new PkgMethodProxy("getVoiceMailNumberForSubscriber"));
        addMethodHook(new PkgMethodProxy("getVoiceMailAlphaTag"));
        addMethodHook(new PkgMethodProxy("getVoiceMailAlphaTagForSubscriber"));
        addMethodHook(new PkgMethodProxy("getLine1Number"));
    }

    @ProxyMethod("getSubscriberId")
    private static class GetSubscriberId extends MethodHook{

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            try {
                if (BuildCompat.isQ()) {
                    return "unknown";
                }
                return method.invoke(who, method, args);
            } catch (Throwable th) {
                return "unknown";
            }
        }
    }


    @ProxyMethod("getLine1NumberForSubscriber")
    public static class GetLine1NumberForSubscriber extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return null;
        }
    }

    @ProxyMethod("getSubscriberIdForSubscriber")
    public static class GetSubscriberIdForSubscriber extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return Md5Utils.md5(BlackBoxCore.getHostPkg());
        }
    }

    @ProxyMethod("getIccSerialNumber")
    public static class GetIccSerialNumber extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return "89860221919704198154";
        }
    }

    @ProxyMethod("getIccSerialNumberForSubscriber")
    public static class GetIccSerialNumberForSubscriber extends GetIccSerialNumber { }
}
