package top.niunaijun.bcore.fake.service;

import android.content.Context;

import java.lang.reflect.Method;

import black.android.os.ServiceManager;
import black.com.android.internal.appwidget.IAppWidgetService;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;
import top.niunaijun.bcore.fake.service.base.ValueMethodProxy;
import top.niunaijun.bcore.utils.MethodParameterUtils;

public class IAppWidgetManagerProxy extends BinderInvocationStub {
    public IAppWidgetManagerProxy() {
        super(ServiceManager.getService.call(Context.APPWIDGET_SERVICE));
    }

    @Override
    protected Object getWho() {
        return IAppWidgetService.Stub.asInterface.call(ServiceManager.getService.call(Context.APPWIDGET_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.APPWIDGET_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceAllAppPkg(args);
        return super.invoke(proxy, method, args);
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ValueMethodProxy("startListening", new int[0]));
        addMethodHook(new ValueMethodProxy("stopListening", 0));
        addMethodHook(new ValueMethodProxy("allocateAppWidgetId", 0));
        addMethodHook(new ValueMethodProxy("deleteAppWidgetId", 0));
        addMethodHook(new ValueMethodProxy("deleteHost", 0));
        addMethodHook(new ValueMethodProxy("deleteAllHosts", 0));
        addMethodHook(new ValueMethodProxy("getAppWidgetViews", null));
        addMethodHook(new ValueMethodProxy("getAppWidgetIdsForHost", null));
        addMethodHook(new ValueMethodProxy("createAppWidgetConfigIntentSender", null));
        addMethodHook(new ValueMethodProxy("updateAppWidgetIds", 0));
        addMethodHook(new ValueMethodProxy("updateAppWidgetOptions", 0));
        addMethodHook(new ValueMethodProxy("getAppWidgetOptions", null));
        addMethodHook(new ValueMethodProxy("partiallyUpdateAppWidgetIds", 0));
        addMethodHook(new ValueMethodProxy("updateAppWidgetProvider", 0));
        addMethodHook(new ValueMethodProxy("notifyAppWidgetViewDataChanged", 0));
        addMethodHook(new ValueMethodProxy("getInstalledProvidersForProfile", null));
        addMethodHook(new ValueMethodProxy("getAppWidgetInfo", null));
        addMethodHook(new ValueMethodProxy("hasBindAppWidgetPermission", false));
        addMethodHook(new ValueMethodProxy("setBindAppWidgetPermission", 0));
        addMethodHook(new ValueMethodProxy("bindAppWidgetId", false));
        addMethodHook(new ValueMethodProxy("bindRemoteViewsService", 0));
        addMethodHook(new ValueMethodProxy("unbindRemoteViewsService", 0));
        addMethodHook(new ValueMethodProxy("getAppWidgetIds", new int[0]));
        addMethodHook(new ValueMethodProxy("isBoundWidgetPackage", false));
    }
}
