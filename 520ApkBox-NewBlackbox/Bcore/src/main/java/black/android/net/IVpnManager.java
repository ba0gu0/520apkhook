package black.android.net;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IVpnManager {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.net.IVpnManager$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
