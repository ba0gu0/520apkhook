package top.niunaijun.bcore.utils.compat;

import android.text.TextUtils;

import black.android.os.SystemProperties;

public class SystemPropertiesCompat {
    public static String get(String key, String def) {
        try {
            return SystemProperties.get0.call(key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public static String get(String key) {
        try {
            return SystemProperties.get1.call(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getInt(String key, int def) {
        try {
            return SystemProperties.getInt.call(key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public static boolean isExist(String key) {
        return !TextUtils.isEmpty(get(key));
    }
}
