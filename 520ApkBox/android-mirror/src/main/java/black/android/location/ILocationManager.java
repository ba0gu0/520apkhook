package black.android.location;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class ILocationManager {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.location.ILocationManager$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
