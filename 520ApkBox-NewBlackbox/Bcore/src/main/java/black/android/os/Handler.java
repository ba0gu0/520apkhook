package black.android.os;

import android.os.Handler.Callback;

import black.Reflector;

public class Handler {
    public static final Reflector REF = Reflector.on("android.os.Handler");

    public static Reflector.FieldWrapper<Callback> mCallback = REF.field("mCallback");
}
