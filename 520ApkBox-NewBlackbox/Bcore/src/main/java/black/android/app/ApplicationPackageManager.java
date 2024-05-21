package black.android.app;

import android.os.IInterface;

import black.Reflector;

public class ApplicationPackageManager {
    public static final Reflector REF = Reflector.on("android.app.ApplicationPackageManager");

    public static Reflector.FieldWrapper<IInterface> mPM = REF.field("mPM");
    public static Reflector.FieldWrapper<Object> mPermissionManager = REF.field("mPermissionManager");
}
