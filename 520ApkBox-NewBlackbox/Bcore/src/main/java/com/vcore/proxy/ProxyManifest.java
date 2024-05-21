package com.vcore.proxy;

import java.util.Locale;

import com.vcore.BlackBoxCore;

public class ProxyManifest {
    public static final int FREE_COUNT = 50;

    public static boolean isProxy(String msg) {
        return getBindProvider().equals(msg) || msg.contains("proxy_content_provider_");
    }

    public static String getBindProvider() {
        return BlackBoxCore.getHostPkg() + ".blackbox.SystemCallProvider";
    }

    public static String getProxyAuthorities(int index) {
        return String.format(Locale.CHINA, "%s.proxy_content_provider_%d", BlackBoxCore.getHostPkg(), index);
    }

    public static String getProxyPendingActivity(int index) {
        return String.format(Locale.CHINA, "com.vcore.proxy.ProxyPendingActivity$P%d", index);
    }

    public static String getProxyActivity(int index) {
        return String.format(Locale.CHINA, "com.vcore.proxy.ProxyActivity$P%d", index);
    }

    public static String TransparentProxyActivity(int index) {
        return String.format(Locale.CHINA, "com.vcore.proxy.TransparentProxyActivity$P%d", index);
    }

    public static String getProxyService(int index) {
        return String.format(Locale.CHINA, "com.vcore.proxy.ProxyService$P%d", index);
    }

    public static String getProxyJobService(int index) {
        return String.format(Locale.CHINA, "com.vcore.proxy.ProxyJobService$P%d", index);
    }

    public static String getProxyFileProvider() {
        return BlackBoxCore.getHostPkg() + ".blackbox.FileProvider";
    }

    public static String getProcessName(int bPid) {
        return BlackBoxCore.getHostPkg() + ":p" + bPid;
    }
}
