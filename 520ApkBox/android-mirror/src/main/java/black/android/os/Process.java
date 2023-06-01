package black.android.os;

import black.Reflector;

public class Process {
    public static final Reflector REF = Reflector.on("android.os.Process");

    public static Reflector.StaticMethodWrapper<Void> setArgV0 = REF.staticMethod("setArgV0", String.class);
}
