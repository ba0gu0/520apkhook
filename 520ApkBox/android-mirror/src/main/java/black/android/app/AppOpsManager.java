package black.android.app;

import android.os.IInterface;

import black.Reflector;

public class AppOpsManager {
    public static final Reflector REF = Reflector.on("android.app.AppOpsManager");

    public static Reflector.FieldWrapper<IInterface> mService = REF.field("mService");
}
