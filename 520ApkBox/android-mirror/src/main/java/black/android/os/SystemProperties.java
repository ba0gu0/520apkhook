package black.android.os;

import black.Reflector;

public class SystemProperties {
    public static final Reflector REF = Reflector.on("android.os.SystemProperties");

    public static Reflector.StaticMethodWrapper<String> get0 = REF.staticMethod("get", String.class, String.class);
    public static Reflector.StaticMethodWrapper<String> get1 = REF.staticMethod("get", String.class);
    public static Reflector.StaticMethodWrapper<Integer> getInt = REF.staticMethod("getInt", String.class, int.class);
}
