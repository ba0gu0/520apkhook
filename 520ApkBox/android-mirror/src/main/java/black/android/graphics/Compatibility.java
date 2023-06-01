package black.android.graphics;

import black.Reflector;

public class Compatibility {
    public static final Reflector REF = Reflector.on("android.graphics.Compatibility");

    public static Reflector.StaticMethodWrapper<Void> setTargetSdkVersion = REF.staticMethod("setTargetSdkVersion", int.class);
}
