package black.android.content;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IContentService {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.content.IContentService$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
