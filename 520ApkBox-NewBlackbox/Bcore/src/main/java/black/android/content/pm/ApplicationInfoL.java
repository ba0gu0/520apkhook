package black.android.content.pm;

import black.Reflector;

public class ApplicationInfoL {
    public static final Reflector REF = Reflector.on("android.content.pm.ApplicationInfo");

    public static Reflector.FieldWrapper<String> primaryCpuAbi = REF.field("primaryCpuAbi");
    public static Reflector.FieldWrapper<String> scanPublicSourceDir = REF.field("scanPublicSourceDir");
    public static Reflector.FieldWrapper<String> scanSourceDir = REF.field("scanSourceDir");
}
