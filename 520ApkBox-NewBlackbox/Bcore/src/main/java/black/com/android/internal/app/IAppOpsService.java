package black.com.android.internal.app;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IAppOpsService {
    public static class Stub {
        public static final Reflector REF = Reflector.on("com.android.internal.app.IAppOpsService$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
