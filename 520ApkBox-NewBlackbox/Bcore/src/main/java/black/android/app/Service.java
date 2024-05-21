package black.android.app;

import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;
import android.os.IBinder;

import black.Reflector;

public class Service {
    public static final Reflector REF = Reflector.on("android.app.Service");

    public static Reflector.MethodWrapper<Void> attach = REF.method("attach", Context.class, ActivityThread.class, String.class, IBinder.class, Application.class, Object.class);
}
