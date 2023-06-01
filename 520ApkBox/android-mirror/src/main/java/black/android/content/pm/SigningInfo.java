package black.android.content.pm;

import android.content.pm.PackageParser.SigningDetails;

import black.Reflector;

public class SigningInfo {
    public static final Reflector REF = Reflector.on("android.content.pm.SigningInfo");

    public static Reflector.ConstructorWrapper<android.content.pm.SigningInfo> _new = REF.constructor(SigningDetails.class);
}
