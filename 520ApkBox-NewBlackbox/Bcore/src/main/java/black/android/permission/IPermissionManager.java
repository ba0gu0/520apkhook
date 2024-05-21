package black.android.permission;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IPermissionManager {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.permission.IPermissionManager$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
