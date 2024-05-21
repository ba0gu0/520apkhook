package black.com.android.internal.infra;

import android.content.Context;

import black.Reflector;

/**
 * @author Findger
 * @function
 * @date :2023/10/13 18:56
 **/
public class AndroidFuture {
    public static final Reflector REF = Reflector.on("com.android.internal.infra.AndroidFuture");
    public static Reflector.MethodWrapper<Boolean> complete = REF.method("complete", Object.class);
    public static Reflector.ConstructorWrapper<Object> ctor = REF.constructor();
}
