package com.vcore.core.env;

import android.content.ComponentName;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import com.vcore.BlackBoxCore;

public class AppSystemEnv {
    private static final List<String> sSystemPackages = new ArrayList<>();
    private static final List<String> sSuPackages = new ArrayList<>();
    private static final List<String> sXposedPackages = new ArrayList<>();
    private static final List<String> sPreInstallPackages = new ArrayList<>();

    static {
        sSystemPackages.add("android");
        sSystemPackages.add("com.google.android.webview");
        sSystemPackages.add("com.google.android.webview.dev");
        sSystemPackages.add("com.google.android.webview.beta");
        sSystemPackages.add("com.google.android.webview.canary");
        sSystemPackages.add("com.android.webview");
        sSystemPackages.add("com.android.camera");
        sSystemPackages.add("com.android.talkback");
        sSystemPackages.add("com.miui.gallery");

        // Google Gboard
        sSystemPackages.add("com.google.android.inputmethod.latin");
        // sSystemPackages.add(BlackBoxCore.getHostPkg());

        // 华为
        sSystemPackages.add("com.huawei.webview");

        // MIUI
        sSystemPackages.add("com.miui.contentcatcher");
        sSystemPackages.add("com.miui.catcherpatch");

        // Oppo
        sSystemPackages.add("com.coloros.safecenter");

        // Su
        sSuPackages.add("com.noshufou.android.su");
        sSuPackages.add("com.noshufou.android.su.elite");
        sSuPackages.add("eu.chainfire.supersu");
        sSuPackages.add("com.koushikdutta.superuser");
        sSuPackages.add("com.thirdparty.superuser");
        sSuPackages.add("com.yellowes.su");

        sXposedPackages.add("de.robv.android.xposed.installer");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < 29){
            //解决Android 9三星浏览器闪退问题
        }else{

        }
    }

    public static boolean isOpenPackage(String packageName) {
        return sSystemPackages.contains(packageName);
    }

    public static boolean isOpenPackage(ComponentName componentName) {
        return componentName != null && isOpenPackage(componentName.getPackageName());
    }

    public static boolean isBlackPackage(String packageName) {
        if (BlackBoxCore.get().isHideRoot() && sSuPackages.contains(packageName)) {
            return true;
        }
        return BlackBoxCore.get().isHideXposed() && sXposedPackages.contains(packageName);
    }

    public static List<String> getPreInstallPackages() {
        return sPreInstallPackages;
    }
}
