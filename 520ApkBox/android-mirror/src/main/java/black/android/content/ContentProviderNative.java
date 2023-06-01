package black.android.content;

import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class ContentProviderNative {
    public static final Reflector REF = Reflector.on("android.content.ContentProviderNative");

    public static Reflector.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
}
