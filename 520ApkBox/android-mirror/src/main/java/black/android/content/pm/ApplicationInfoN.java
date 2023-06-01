package black.android.content.pm;

import black.Reflector;

public class ApplicationInfoN {
    public static final Reflector REF = Reflector.on("android.content.pm.ApplicationInfo");

    public static Reflector.FieldWrapper<String> credentialEncryptedDataDir = REF.field("credentialEncryptedDataDir");
    public static Reflector.FieldWrapper<String> credentialProtectedDataDir = REF.field("credentialProtectedDataDir");
    public static Reflector.FieldWrapper<String> deviceProtectedDataDir = REF.field("deviceProtectedDataDir");
}
