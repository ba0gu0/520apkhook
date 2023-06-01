package black.android.app;

import android.app.Activity;
import android.app.Application;
import android.app.ContentProviderHolder;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;

import java.util.List;
import java.util.Map;

import black.Reflector;

public class ActivityThread {
    public static final Reflector REF = Reflector.on("android.app.ActivityThread");

    public static Reflector.FieldWrapper<IInterface> sPackageManager = REF.field("sPackageManager");
    public static Reflector.FieldWrapper<IInterface> sPermissionManager = REF.field("sPermissionManager");
    public static Reflector.FieldWrapper<Map<IBinder, Object>> mActivities = REF.field("mActivities");
    public static Reflector.FieldWrapper<Object> mBoundApplication = REF.field("mBoundApplication");
    public static Reflector.FieldWrapper<Handler> mH = REF.field("mH");
    public static Reflector.FieldWrapper<Application> mInitialApplication = REF.field("mInitialApplication");
    public static Reflector.FieldWrapper<Instrumentation> mInstrumentation = REF.field("mInstrumentation");
    public static Reflector.FieldWrapper<Map<?, ?>> mProviderMap = REF.field("mProviderMap");

    public static Reflector.StaticMethodWrapper<Object> currentActivityThread = REF.staticMethod("currentActivityThread");

    public static Reflector.MethodWrapper<IBinder> getApplicationThread = REF.method("getApplicationThread");
    public static Reflector.MethodWrapper<Object> getSystemContext = REF.method("getSystemContext");
    public static Reflector.MethodWrapper<Object> getLaunchingActivity = REF.method("getLaunchingActivity", IBinder.class);
    public static Reflector.MethodWrapper<Void> performNewIntents = REF.method("performNewIntents", IBinder.class, List.class);
    public static Reflector.MethodWrapper<Void> installProvider = REF.method("installProvider", Context.class, ContentProviderHolder.class, ProviderInfo.class, boolean.class, boolean.class, boolean.class);

    public static class CreateServiceData {
        public static final Reflector REF = Reflector.on("android.app.ActivityThread$CreateServiceData");

        public static Reflector.FieldWrapper<ServiceInfo> info = REF.field("info");
    }

    public static class H {
        public static final Reflector REF = Reflector.on("android.app.ActivityThread$H");

        public static Reflector.FieldWrapper<Integer> CREATE_SERVICE = REF.field("CREATE_SERVICE");
        public static Reflector.FieldWrapper<Integer> EXECUTE_TRANSACTION = REF.field("EXECUTE_TRANSACTION");
        public static Reflector.FieldWrapper<Integer> LAUNCH_ACTIVITY = REF.field("LAUNCH_ACTIVITY");
    }

    public static class AppBindData {
        public static final Reflector REF = Reflector.on("android.app.ActivityThread$AppBindData");

        public static Reflector.FieldWrapper<ApplicationInfo> appInfo = REF.field("appInfo");
        public static Reflector.FieldWrapper<Object> info = REF.field("info");
        public static Reflector.FieldWrapper<ComponentName> instrumentationName = REF.field("instrumentationName");
        public static Reflector.FieldWrapper<String> processName = REF.field("processName");
        public static Reflector.FieldWrapper<List<ProviderInfo>> providers = REF.field("providers");
    }

    public static class ProviderClientRecordP {
        public static final Reflector REF = Reflector.on("android.app.ActivityThread$ProviderClientRecord");

        public static Reflector.FieldWrapper<String[]> mNames = REF.field("mNames");
        public static Reflector.FieldWrapper<IInterface> mProvider = REF.field("mProvider");
    }

    public static class ActivityClientRecord {
        public static final Reflector REF = Reflector.on("android.app.ActivityThread$ActivityClientRecord");

        public static Reflector.FieldWrapper<Activity> activity = REF.field("activity");
        public static Reflector.FieldWrapper<ActivityInfo> activityInfo = REF.field("activityInfo");
        public static Reflector.FieldWrapper<Intent> intent = REF.field("intent");
        public static Reflector.FieldWrapper<IBinder> token = REF.field("token");
        public static Reflector.FieldWrapper<Object> packageInfo = REF.field("packageInfo");
    }

    public static class AndroidOs {
        public static final Reflector REF = Reflector.on("android.app.ActivityThread$AndroidOs");
    }
}
