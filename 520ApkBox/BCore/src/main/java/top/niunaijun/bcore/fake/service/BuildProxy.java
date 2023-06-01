package top.niunaijun.bcore.fake.service;

import black.android.os.Build;
import top.niunaijun.bcore.fake.hook.ClassInvocationStub;

public class BuildProxy extends ClassInvocationStub {

    @Override
    protected Object getWho() {
        return Build.REF;
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        Build.BOARD.set("umi");
        Build.BRAND.set("Xiaomi");
        Build.DEVICE.set("umi");
        Build.DISPLAY.set("QKQ1.191117.002 test-keys");
        Build.HOST.set("c5-miui-ota-bd074.bj");
        Build.ID.set("QKQ1.191117.002");
        Build.MANUFACTURER.set("Xiaomi");
        Build.MODEL.set("Mi 10");
        Build.PRODUCT.set("umi");
        Build.TAGS.set("release-keys");
        Build.TYPE.set("user");
        Build.USER.set("builder");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
