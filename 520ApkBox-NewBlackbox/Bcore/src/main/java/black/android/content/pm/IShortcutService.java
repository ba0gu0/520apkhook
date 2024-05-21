package black.android.content.pm;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IShortcutService {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.content.pm.IShortcutService$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
