package black.android.os;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IVibratorManagerService {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.os.IVibratorManagerService$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
