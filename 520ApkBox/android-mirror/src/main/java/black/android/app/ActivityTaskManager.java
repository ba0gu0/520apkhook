package black.android.app;

import black.Reflector;

public class ActivityTaskManager {
    public static final Reflector REF = Reflector.on("android.app.ActivityTaskManager");

    public static Reflector.FieldWrapper<Object> IActivityTaskManagerSingleton = REF.field("IActivityTaskManagerSingleton");
}
