package black.android.app.servertransaction;

import android.os.IBinder;

import java.util.List;

import black.Reflector;

public class ClientTransaction {
    public static final Reflector REF = Reflector.on("android.app.servertransaction.ClientTransaction");

    public static Reflector.FieldWrapper<List<Object>> mActivityCallbacks = REF.field("mActivityCallbacks");
    public static Reflector.FieldWrapper<IBinder> mActivityToken = REF.field("mActivityToken");
}
