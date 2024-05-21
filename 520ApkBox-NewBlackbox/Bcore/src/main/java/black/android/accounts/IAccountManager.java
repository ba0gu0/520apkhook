package black.android.accounts;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IAccountManager {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.accounts.IAccountManager$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
