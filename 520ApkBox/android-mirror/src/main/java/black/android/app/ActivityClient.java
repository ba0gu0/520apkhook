package black.android.app;

import android.os.IInterface;

import black.Reflector;

public class ActivityClient {
    public static final Reflector REF = Reflector.on("android.app.ActivityClient");

    public static Reflector.FieldWrapper<Object> INTERFACE_SINGLETON = REF.field("INTERFACE_SINGLETON");

    public static Reflector.StaticMethodWrapper<Object> getInstance = REF.staticMethod("getInstance");
    public static Reflector.StaticMethodWrapper<Object> getActivityClientController = REF.staticMethod("getActivityClientController");

    public static class ActivityClientControllerSingleton {
        public static final Reflector REF = Reflector.on("android.app.ActivityClient$ActivityClientControllerSingleton");

        public static Reflector.FieldWrapper<IInterface> mKnownInstance = REF.field("mKnownInstance");
    }
}
