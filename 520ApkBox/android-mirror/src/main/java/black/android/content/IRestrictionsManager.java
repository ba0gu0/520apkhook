package black.android.content;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IRestrictionsManager {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.content.IRestrictionsManager$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
