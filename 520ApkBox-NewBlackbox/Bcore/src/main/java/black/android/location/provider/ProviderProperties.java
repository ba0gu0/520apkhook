package black.android.location.provider;

import black.Reflector;

public class ProviderProperties {
    public static final Reflector REF = Reflector.on("android.location.provider.ProviderProperties");

    public static Reflector.FieldWrapper<Boolean> mHasNetworkRequirement = REF.field("mHasNetworkRequirement");
    public static Reflector.FieldWrapper<Boolean> mHasCellRequirement = REF.field("mHasCellRequirement");
}
