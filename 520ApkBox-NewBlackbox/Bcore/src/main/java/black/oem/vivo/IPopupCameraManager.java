package black.oem.vivo;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

/**
 * @author Findger
 * @function
 * @date :2023/10/8 20:21
 **/
public class IPopupCameraManager {
    public static final Reflector TYPE = Reflector.on("vivo.app.popupcamera.IPopupCameraManager");

    public static class Stub {
        public static final Reflector TYPE = Reflector.on("vivo.app.popupcamera.IPopupCameraManager$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = TYPE.staticMethod("asInterface", IBinder.class);
    }
}
