package black.android.app.job;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IJobScheduler {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.app.job.IJobScheduler$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
