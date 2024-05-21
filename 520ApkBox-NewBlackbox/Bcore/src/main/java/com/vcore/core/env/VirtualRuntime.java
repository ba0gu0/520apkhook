package com.vcore.core.env;

import android.content.pm.ApplicationInfo;

import black.android.ddm.DdmHandleAppName;
import black.android.os.Process;

public class VirtualRuntime {
    private static String sInitialPackageName;
    private static String sProcessName;

    public static String getProcessName() {
        return sProcessName;
    }

    public static String getInitialPackageName() {
        return sInitialPackageName;
    }

    public static void setupRuntime(String processName, ApplicationInfo appInfo) {
        if (sProcessName != null) {
            return;
        }

        sInitialPackageName = appInfo.packageName;
        sProcessName = processName;
        Process.setArgV0.call(processName);
        DdmHandleAppName.setAppName.call(processName, 0);
    }
}
