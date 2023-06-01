package black.android.view;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IGraphicsStats {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.view.IGraphicsStats$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
