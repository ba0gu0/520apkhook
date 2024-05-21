package black.android.content.pm;

import android.content.pm.PackageParser;

import black.Reflector;

public class PackageParserNougat {
    public static final Reflector REF = Reflector.on("android.content.pm.PackageParser");

    public static Reflector.StaticMethodWrapper<Void> collectCertificates = REF.staticMethod("collectCertificates", PackageParser.Package.class, int.class);
}
