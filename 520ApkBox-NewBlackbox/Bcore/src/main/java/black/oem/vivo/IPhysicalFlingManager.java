package black.oem.vivo;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

/**
 * @author Findger
 * @function
 * @date :2023/10/8 20:12
 **/
public class IPhysicalFlingManager {
    public static final Reflector TYPE = Reflector.on("vivo.app.physicalfling.IPhysicalFlingManager");

    public static class Stub {
        public static final Reflector TYPE = Reflector.on("vivo.app.physicalfling.IPhysicalFlingManager$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = TYPE.staticMethod("asInterface", IBinder.class);
    }
}
