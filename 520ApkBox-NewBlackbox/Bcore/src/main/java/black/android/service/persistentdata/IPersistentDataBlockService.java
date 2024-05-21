package black.android.service.persistentdata;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IPersistentDataBlockService {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.service.persistentdata.IPersistentDataBlockService$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
