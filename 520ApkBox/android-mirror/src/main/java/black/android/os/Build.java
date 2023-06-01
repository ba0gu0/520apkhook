package black.android.os;

import black.Reflector;

public class Build {
    public static final Reflector REF = Reflector.on("android.os.Build");

    public static Reflector.FieldWrapper<String> BOARD = REF.field("BOARD");
    public static Reflector.FieldWrapper<String> BRAND = REF.field("BRAND");
    public static Reflector.FieldWrapper<String> DEVICE = REF.field("DEVICE");
    public static Reflector.FieldWrapper<String> DISPLAY = REF.field("DISPLAY");
    public static Reflector.FieldWrapper<String> HOST = REF.field("HOST");
    public static Reflector.FieldWrapper<String> ID = REF.field("ID");
    public static Reflector.FieldWrapper<String> MANUFACTURER = REF.field("MANUFACTURER");
    public static Reflector.FieldWrapper<String> MODEL = REF.field("MODEL");
    public static Reflector.FieldWrapper<String> PRODUCT = REF.field("PRODUCT");
    public static Reflector.FieldWrapper<String> TAGS = REF.field("TAGS");
    public static Reflector.FieldWrapper<String> TYPE = REF.field("TYPE");
    public static Reflector.FieldWrapper<String> USER = REF.field("USER");
}
