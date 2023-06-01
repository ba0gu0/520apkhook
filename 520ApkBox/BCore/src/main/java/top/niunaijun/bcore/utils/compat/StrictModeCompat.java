package top.niunaijun.bcore.utils.compat;

import black.android.os.StrictMode;

public class StrictModeCompat {
    public static final int DETECT_VM_FILE_URI_EXPOSURE = StrictMode.DETECT_VM_FILE_URI_EXPOSURE.get() == null ?
            (0x20 << 8) : StrictMode.DETECT_VM_FILE_URI_EXPOSURE.get();

    public static final int PENALTY_DEATH_ON_FILE_URI_EXPOSURE = StrictMode.PENALTY_DEATH_ON_FILE_URI_EXPOSURE.get() == null ?
            (0x04 << 24) : StrictMode.PENALTY_DEATH_ON_FILE_URI_EXPOSURE.get();

    public static void disableDeathOnFileUriExposure() {
        try {
            StrictMode.disableDeathOnFileUriExposure.call();
        } catch (Throwable e) {
            try {
                int sVmPolicyMask = StrictMode.sVmPolicyMask.get();
                sVmPolicyMask &= ~(DETECT_VM_FILE_URI_EXPOSURE | PENALTY_DEATH_ON_FILE_URI_EXPOSURE);
                StrictMode.sVmPolicyMask.set(sVmPolicyMask);
            } catch (Throwable e2) {
                e2.printStackTrace();
            }
        }
    }
}
