package black.android.app;

import java.util.List;

import black.Reflector;

public class NotificationChannelGroup {
    public static final Reflector REF = Reflector.on("android.app.NotificationChannelGroup");

    public static Reflector.FieldWrapper<List<android.app.NotificationChannel>> mChannels = REF.field("mChannels");
    public static Reflector.FieldWrapper<String> mId = REF.field("mId");
}
