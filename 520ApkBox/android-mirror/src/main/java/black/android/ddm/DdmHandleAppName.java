package black.android.ddm;

import black.Reflector;

public class DdmHandleAppName {
    public static final Reflector REF = Reflector.on("android.ddm.DdmHandleAppName");

    public static Reflector.StaticMethodWrapper<Void> setAppName = REF.staticMethod("setAppName", String.class, int.class);
}
