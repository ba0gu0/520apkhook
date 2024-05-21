package black.com.android.internal.os;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IVibratorService {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.os.IVibratorService$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
