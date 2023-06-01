package black.android.content;

import black.Reflector;

public class ContentResolver {
    public static final Reflector REF = Reflector.on("android.content.ContentResolver");

    public static Reflector.FieldWrapper<String> mPackageName = REF.field("mPackageName");
}
