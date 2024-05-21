package black.android.app;

import black.Reflector;

public class NotificationO {
    public static final Reflector REF = Reflector.on("android.app.Notification");

    public static Reflector.FieldWrapper<String> mChannelId = REF.field("mChannelId");
    public static Reflector.FieldWrapper<String> mGroupKey = REF.field("mGroupKey");
}
