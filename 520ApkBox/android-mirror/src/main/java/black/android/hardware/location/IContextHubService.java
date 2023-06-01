package black.android.hardware.location;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IContextHubService {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.hardware.location.IContextHubService$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
