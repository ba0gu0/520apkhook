package black.android.os;

import black.Reflector;

public class StrictMode {
    public static final Reflector REF = Reflector.on("android.os.StrictMode");

    public static Reflector.FieldWrapper<Integer> DETECT_VM_FILE_URI_EXPOSURE = REF.field("DETECT_VM_FILE_URI_EXPOSURE");
    public static Reflector.FieldWrapper<Integer> PENALTY_DEATH_ON_FILE_URI_EXPOSURE = REF.field("PENALTY_DEATH_ON_FILE_URI_EXPOSURE");
    public static Reflector.FieldWrapper<Integer> sVmPolicyMask = REF.field("sVmPolicyMask");

    public static Reflector.StaticMethodWrapper<Void> disableDeathOnFileUriExposure = REF.staticMethod("disableDeathOnFileUriExposure");
}
