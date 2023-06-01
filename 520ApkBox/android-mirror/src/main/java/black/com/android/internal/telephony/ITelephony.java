package black.com.android.internal.telephony;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class ITelephony {
    public static class Stub {
        public static final Reflector REF = Reflector.on("com.android.internal.telephony.ITelephony$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
