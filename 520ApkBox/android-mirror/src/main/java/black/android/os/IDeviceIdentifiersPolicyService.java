package black.android.os;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IDeviceIdentifiersPolicyService {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.os.IDeviceIdentifiersPolicyService$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
