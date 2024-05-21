package black.android.net;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IConnectivityManager {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.net.IConnectivityManager$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
