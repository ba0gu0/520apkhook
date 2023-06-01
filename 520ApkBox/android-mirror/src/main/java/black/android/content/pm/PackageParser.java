package black.android.content.pm;

import android.content.pm.ApplicationInfo;
import android.content.pm.Signature;
import android.util.DisplayMetrics;

import java.io.File;

import black.Reflector;

public class PackageParser {
    public static final Reflector REF = Reflector.on("android.content.pm.PackageParser");

    public static Reflector.MethodWrapper<Void> collectCertificates = REF.method("collectCertificates", android.content.pm.PackageParser.Package.class, int.class);
    public static Reflector.MethodWrapper<android.content.pm.PackageParser.Package> parsePackage = REF.method("parsePackage", File.class, String.class, DisplayMetrics.class, int.class);

    public static class Package {
        public static final Reflector REF = Reflector.on("android.content.pm.PackageParser$Package");

        public static Reflector.FieldWrapper<ApplicationInfo> applicationInfo = REF.field("applicationInfo");
    }

    public static class SigningDetails {
        public static final Reflector REF = Reflector.on("android.content.pm.PackageParser$SigningDetails");

        public static Reflector.FieldWrapper<Signature[]> signatures = REF.field("signatures");
    }
}
