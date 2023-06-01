package top.niunaijun.bcore.fake.service.context;

import android.location.Location;

import java.lang.reflect.Method;
import java.util.List;

import top.niunaijun.bcore.app.BActivityThread;
import top.niunaijun.bcore.fake.frameworks.BLocationManager;
import top.niunaijun.bcore.fake.hook.ClassInvocationStub;
import top.niunaijun.bcore.fake.hook.MethodHook;
import top.niunaijun.bcore.fake.hook.ProxyMethod;

public class LocationListenerProxy extends ClassInvocationStub {
    public static final String TAG = "LocationListenerProxy";
    private Object mBase;

    public Object wrapper(final Object locationListenerProxy) {
        mBase = locationListenerProxy;
        injectHook();
        return getProxyInvocation();
    }

    @Override
    protected Object getWho() {
        return mBase;
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) { }

    @ProxyMethod("onLocationChanged")
    public static class OnLocationChanged extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args[0] instanceof List) {
                List<Location> locations = (List<Location>) args[0];
                locations.clear();
                locations.add(BLocationManager.get().getLocation(BActivityThread.getUserId(), BActivityThread.getAppPackageName()).convert2SystemLocation());
                args[0] = locations;
            } else if (args[0] instanceof Location) {
                args[0] = BLocationManager.get().getLocation(BActivityThread.getUserId(), BActivityThread.getAppPackageName()).convert2SystemLocation();
            }
            return method.invoke(who, args);
        }
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
