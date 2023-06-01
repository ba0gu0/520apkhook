package black.android.media.session;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class ISessionManager {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.media.session.ISessionManager$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
