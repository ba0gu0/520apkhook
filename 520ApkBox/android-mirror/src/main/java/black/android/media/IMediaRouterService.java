package black.android.media;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IMediaRouterService {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.media.IMediaRouterService$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
