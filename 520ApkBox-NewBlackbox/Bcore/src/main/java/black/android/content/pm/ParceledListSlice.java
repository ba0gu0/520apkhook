package black.android.content.pm;

import java.util.List;

import black.Reflector;

public class ParceledListSlice {
    public static final Reflector REF = Reflector.on("android.content.pm.ParceledListSlice");

    public static Reflector.ConstructorWrapper<Object> _new0 = REF.constructor();
    public static Reflector.ConstructorWrapper<Object> _new1 = REF.constructor(List.class);

    public static Reflector.MethodWrapper<Boolean> append = REF.method("append", Object.class);
    public static Reflector.MethodWrapper<List<?>> getList = REF.method("getList");
    public static Reflector.MethodWrapper<Void> setLastSlice = REF.method("setLastSlice", boolean.class);
}
