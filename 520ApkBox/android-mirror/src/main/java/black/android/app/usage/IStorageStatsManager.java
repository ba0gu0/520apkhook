package black.android.app.usage;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IStorageStatsManager {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.app.usage.IStorageStatsManager$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
