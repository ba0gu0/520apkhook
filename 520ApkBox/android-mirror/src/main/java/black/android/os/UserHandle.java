package black.android.os;

import black.Reflector;

public class UserHandle {
    public static final Reflector REF = Reflector.on("android.os.UserHandle");

    public static Reflector.StaticMethodWrapper<Integer> myUserId = REF.staticMethod("myUserId");
}
