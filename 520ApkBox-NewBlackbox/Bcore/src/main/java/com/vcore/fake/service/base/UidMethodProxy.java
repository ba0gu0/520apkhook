package com.vcore.fake.service.base;

import java.lang.reflect.Method;

import com.vcore.BlackBoxCore;
import com.vcore.app.BActivityThread;
import com.vcore.fake.hook.MethodHook;

public class UidMethodProxy extends MethodHook {
    private final int index;
    private final String name;

    public UidMethodProxy(String name, int index) {
        this.index = index;
        this.name = name;
    }

    @Override
    protected String getMethodName() {
        return name;
    }

    @Override
    protected Object hook(Object who, Method method, Object[] args) throws Throwable {
        int uid = (int) args[index];
        if (uid == BActivityThread.getBUid()) {
            args[index] = BlackBoxCore.getHostUid();
        }
        return method.invoke(who, args);
    }
}
