package black.android.app;

import android.app.Application;
import android.app.Instrumentation;
import android.content.IIntentReceiver;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;

import java.lang.ref.WeakReference;

import black.Reflector;

public class LoadedApk {
    public static final Reflector REF = Reflector.on("android.app.LoadedApk");

    public static Reflector.FieldWrapper<ApplicationInfo> mApplicationInfo = REF.field("mApplicationInfo");
    public static Reflector.FieldWrapper<Boolean> mSecurityViolation = REF.field("mSecurityViolation");

    public static Reflector.MethodWrapper<ClassLoader> getClassLoader = REF.method("getClassLoader");
    public static Reflector.MethodWrapper<Application> makeApplication = REF.method("makeApplication", boolean.class, Instrumentation.class);

    public static class ServiceDispatcher {
        public static final Reflector REF = Reflector.on("android.app.LoadedApk$ServiceDispatcher");

        public static Reflector.FieldWrapper<ServiceConnection> mConnection = REF.field("mConnection");

        public static class InnerConnection {
            public static final Reflector REF = Reflector.on("android.app.LoadedApk$ServiceDispatcher$InnerConnection");

            public static Reflector.FieldWrapper<WeakReference<?>> mDispatcher = REF.field("mDispatcher");
        }
    }

    public static class ReceiverDispatcher {
        public static final Reflector REF = Reflector.on("android.app.LoadedApk$ReceiverDispatcher");

        public static Reflector.FieldWrapper<IIntentReceiver> mIIntentReceiver = REF.field("mIIntentReceiver");

        public static class InnerReceiver {
            public static final Reflector REF = Reflector.on("android.app.LoadedApk$ReceiverDispatcher$InnerReceiver");

            public static Reflector.FieldWrapper<WeakReference<?>> mDispatcher = REF.field("mDispatcher");
        }
    }
}
