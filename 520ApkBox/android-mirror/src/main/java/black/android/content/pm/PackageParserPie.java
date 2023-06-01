package black.android.content.pm;

import android.content.pm.PackageParser;

import java.io.File;

import black.Reflector;

public class PackageParserPie {
    public static final Reflector REF = Reflector.on("android.content.pm.PackageParser");

    public static Reflector.ConstructorWrapper<PackageParser> _new = REF.constructor();

    public static Reflector.StaticMethodWrapper<Void> collectCertificates = REF.staticMethod("collectCertificates", PackageParser.Package.class, boolean.class);

    public static Reflector.MethodWrapper<PackageParser.Package> parsePackage = REF.method("parsePackage", File.class, int.class);
}
