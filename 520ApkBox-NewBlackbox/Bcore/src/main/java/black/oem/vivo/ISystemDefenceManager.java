package black.oem.vivo;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

/**
 * @author Findger
 * @function
 * @date :2023/10/8 20:32
 **/
public class ISystemDefenceManager {
    public static final Reflector TYPE = Reflector.on("vivo.app.systemdefence.ISystemDefenceManager");

    public static class Stub {
        public static final Reflector TYPE = Reflector.on("vivo.app.systemdefence.ISystemDefenceManager$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = TYPE.staticMethod("asInterface", IBinder.class);
    }
}
