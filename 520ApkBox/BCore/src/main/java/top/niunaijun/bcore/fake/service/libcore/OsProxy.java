package top.niunaijun.bcore.fake.service.libcore;

import android.os.Process;

import java.lang.reflect.Method;
import java.util.Objects;

import black.Reflector;
import black.libcore.io.Libcore;
import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.app.BActivityThread;
import top.niunaijun.bcore.fake.hook.ClassInvocationStub;
import top.niunaijun.bcore.fake.hook.MethodHook;
import top.niunaijun.bcore.fake.hook.ProxyMethod;
import top.niunaijun.bcore.fake.hook.ProxyMethods;

public class OsProxy extends ClassInvocationStub {
    public static final String TAG = "OsProxy";
    private final Object mBase;

    public OsProxy() {
        this.mBase = Libcore.os.get();
    }

    @Override
    protected Object getWho() {
        return mBase;
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        Libcore.os.set(proxyInvocation);
    }

    @Override
    public boolean isBadEnv() {
        return Libcore.os.get() != getProxyInvocation();
    }

    @ProxyMethod("getuid")
    public static class GetUID extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int callUid = (int) method.invoke(who, args);
            return getFakeUid(callUid);
        }
    }

    @ProxyMethods({"lstat", "stat"})
    public static class Stat extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Object invoke;
            try {
                invoke = method.invoke(who, args);
            } catch (Throwable e) {
                throw Objects.requireNonNull(e.getCause());
            }

            Reflector.on("android.system.StructStat")
                    .field("st_uid").set(invoke, getFakeUid(-1));
            return invoke;
        }
    }

    private static int getFakeUid(int callUid) {
        if (callUid > 0 && callUid <= Process.FIRST_APPLICATION_UID) {
            return callUid;
        }

        if (BActivityThread.isThreadInit() && BActivityThread.currentActivityThread().isInit()) {
            return BActivityThread.getBAppId();
        }
        return BlackBoxCore.getHostUid();
    }
}
