package black.android.bluetooth;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IBluetoothManager {
    public static class Stub {
        public static final Reflector REF = Reflector.on("android.bluetooth.IBluetoothManager$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
