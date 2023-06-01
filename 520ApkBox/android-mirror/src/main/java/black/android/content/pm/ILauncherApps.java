package black.android.content.pm;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class ILauncherApps {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.content.pm.ILauncherApps$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
