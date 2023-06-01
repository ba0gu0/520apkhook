package black.com.android.internal.appwidget;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IAppWidgetService {
    public static class Stub {
        public static final Reflector REF = Reflector.on("com.android.internal.appwidget.IAppWidgetService$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
