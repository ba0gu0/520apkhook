package top.niunaijun.bcore.utils;

import static android.content.pm.ActivityInfo.LAUNCH_SINGLE_INSTANCE;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;

import java.util.Objects;

import top.niunaijun.bcore.app.BActivityThread;

public class ComponentUtils {
    public static boolean isRequestInstall(Intent intent) {
        return "application/vnd.android.package-archive".equals(intent.getType());
    }

    public static boolean isSelf(Intent intent) {
        ComponentName component = intent.getComponent();
        if (component == null || BActivityThread.getAppPackageName() == null) {
            return false;
        }
        return component.getPackageName().equals(BActivityThread.getAppPackageName());
    }

    public static boolean isSelf(Intent[] intent) {
        for (Intent intent1 : intent) {
            if (!isSelf(intent1)) {
                return false;
            }
        }
        return true;
    }

    public static String getTaskAffinity(ActivityInfo info) {
        if (info.launchMode == LAUNCH_SINGLE_INSTANCE) {
            return "-SingleInstance-" + info.packageName + "/" + info.name;
        } else if (info.taskAffinity == null && info.applicationInfo.taskAffinity == null) {
            return info.packageName;
        } else if (info.taskAffinity != null) {
            return info.taskAffinity;
        }
        return info.applicationInfo.taskAffinity;
    }

    public static boolean intentFilterEquals(Intent a, Intent b) {
        if (a != null && b != null) {
            if (!Objects.equals(a.getAction(), b.getAction())) {
                return false;
            }

            if (!Objects.equals(a.getData(), b.getData())) {
                return false;
            }

            if (!Objects.equals(a.getType(), b.getType())) {
                return false;
            }

            Object pkgA = a.getPackage();
            if (pkgA == null && a.getComponent() != null) {
                pkgA = a.getComponent().getPackageName();
            }

            String pkgB = b.getPackage();
            if (pkgB == null && b.getComponent() != null) {
                pkgB = b.getComponent().getPackageName();
            }

            if (!Objects.equals(pkgA, pkgB)) {
                return false;
            }

            if (!Objects.equals(a.getComponent(), b.getComponent())) {
                return false;
            }
            return Objects.equals(a.getCategories(), b.getCategories());
        }
        return true;
    }

    public static String getProcessName(ComponentInfo componentInfo) {
        String processName = componentInfo.processName;
        if (processName == null) {
            processName = componentInfo.packageName;
            componentInfo.processName = processName;
        }
        return processName;
    }

    public static ComponentName toComponentName(ComponentInfo componentInfo) {
        return new ComponentName(componentInfo.packageName, componentInfo.name);
    }
}
