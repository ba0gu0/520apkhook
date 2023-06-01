package black.android.app;

import android.content.ComponentName;
import android.os.IBinder;

import black.Reflector;

public class IServiceConnectionO {
    public static final Reflector REF = Reflector.on("android.app.IServiceConnection");

    public static Reflector.MethodWrapper<Void> connected = REF.method("connected", ComponentName.class, IBinder.class, boolean.class);
}
