package black.android.content.pm;

import android.content.pm.PackageParser;
import android.content.pm.PackageParser.Package;

import java.io.File;

import black.Reflector;

public class PackageParserMarshmallow {
    public static final Reflector REF = Reflector.on("android.content.pm.PackageParser");

    public static Reflector.ConstructorWrapper<PackageParser> _new = REF.constructor();

    public static Reflector.MethodWrapper<Void> collectCertificates = REF.method("collectCertificates", Package.class, int.class);
    public static Reflector.MethodWrapper<Package> parsePackage = REF.method("parsePackage", File.class, int.class);
}
