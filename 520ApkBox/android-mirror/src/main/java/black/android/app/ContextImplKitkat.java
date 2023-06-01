package black.android.app;

import black.Reflector;

public class ContextImplKitkat {
    public static final Reflector REF = Reflector.on("android.app.ContextImpl");

    public static Reflector.FieldWrapper<String> mOpPackageName = REF.field("mOpPackageName");
}
