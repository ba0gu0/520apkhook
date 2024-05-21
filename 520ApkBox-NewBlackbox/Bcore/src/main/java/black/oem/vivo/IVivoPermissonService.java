package black.oem.vivo;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

/**
 * @author Findger
 * @function
 * @date :2023/10/8 20:37
 **/
public class IVivoPermissonService {
    public static final Reflector TYPE = Reflector.on("vivo.app.security.IVivoPermissionService");

    public static class Stub {
        public static final Reflector TYPE = Reflector.on("vivo.app.security.IVivoPermissionService$Stub");
        public static Reflector.StaticMethodWrapper<IInterface> asInterface = TYPE.staticMethod("asInterface", IBinder.class);
    }
}
