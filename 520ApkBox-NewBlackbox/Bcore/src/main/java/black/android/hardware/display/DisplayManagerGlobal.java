package black.android.hardware.display;

import android.os.IInterface;

import black.Reflector;

public class DisplayManagerGlobal {
    public static final Reflector REF = Reflector.on("android.hardware.display.DisplayManagerGlobal");

    public static Reflector.FieldWrapper<IInterface> mDm = REF.field("mDm");

    public static Reflector.StaticMethodWrapper<Object> getInstance = REF.staticMethod("getInstance");
}
