package black.android.content;

import black.Reflector;

public class AttributionSource {
    public static final Reflector REF = Reflector.on("android.content.AttributionSource");

    public static Reflector.FieldWrapper<Object> mAttributionSourceState = REF.field("mAttributionSourceState");

    public static Reflector.MethodWrapper<Object> getNext = REF.method("getNext");
}
