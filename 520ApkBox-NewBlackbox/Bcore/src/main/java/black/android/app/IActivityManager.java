package black.android.app;

import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;

import black.Reflector;

public class IActivityManager {
    public static final Reflector REF = Reflector.on("android.app.IActivityManager");

    public static Reflector.MethodWrapper<Integer> getTaskForActivity = REF.method("getTaskForActivity", IBinder.class, boolean.class);
    public static Reflector.MethodWrapper<Void> setRequestedOrientation = REF.method("setRequestedOrientation", IBinder.class, int.class);
    public static Reflector.MethodWrapper<Integer> startActivity = REF.method("startActivity", Reflector.findClass("android.app.IApplicationThread"), String.class, Intent.class, String.class, IBinder.class, String.class, int.class, int.class, Reflector.findClass("android.app.ProfilerInfo"), Bundle.class);

    public static class ContentProviderHolder {
        public static final Reflector REF = Reflector.on("android.app.IActivityManager$ContentProviderHolder");

        public static Reflector.FieldWrapper<ProviderInfo> info = REF.field("info");
        public static Reflector.FieldWrapper<IInterface> provider = REF.field("provider");
    }
}
