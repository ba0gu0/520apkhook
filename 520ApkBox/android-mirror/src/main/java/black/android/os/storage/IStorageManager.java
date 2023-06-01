package black.android.os.storage;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IStorageManager {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.os.storage.IStorageManager$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
