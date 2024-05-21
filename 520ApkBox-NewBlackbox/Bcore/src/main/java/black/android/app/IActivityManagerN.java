package black.android.app;

import android.content.Intent;
import android.os.IBinder;

import black.Reflector;

public class IActivityManagerN {
    public static final Reflector REF = Reflector.on("android.app.IActivityManager");

    public static Reflector.MethodWrapper<Boolean> finishActivity = REF.method("finishActivity", IBinder.class, int.class, Intent.class, int.class);
}
