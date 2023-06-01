package top.niunaijun.bcore.utils.compat;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;
import static android.os.Build.VERSION_CODES.M;
import static android.os.Build.VERSION_CODES.N;

import android.content.pm.PackageParser;
import android.content.pm.PackageParser.Package;
import android.os.Build;
import android.util.DisplayMetrics;

import java.io.File;

import black.android.content.pm.PackageParserLollipop;
import black.android.content.pm.PackageParserLollipop22;
import black.android.content.pm.PackageParserMarshmallow;
import black.android.content.pm.PackageParserNougat;
import black.android.content.pm.PackageParserPie;
import top.niunaijun.bcore.BlackBoxCore;

public class PackageParserCompat {
    private static final int API_LEVEL = Build.VERSION.SDK_INT;

    public static PackageParser createParser() {
        if (BuildCompat.isQ()) {
            PackageParser packageParser = PackageParserPie._new.newInstance();
            packageParser.setCallback(new PackageParser.CallbackImpl(BlackBoxCore.getPackageManager()));
            return packageParser;
        } else if (API_LEVEL >= 28) {
            return PackageParserPie._new.newInstance();
        } else if (API_LEVEL >= M) {
            return PackageParserMarshmallow._new.newInstance();
        } else if (API_LEVEL >= LOLLIPOP_MR1) {
            return PackageParserLollipop22._new.newInstance();
        } else if (API_LEVEL >= LOLLIPOP) {
            return PackageParserLollipop._new.newInstance();
        }
        return null;
    }

    public static Package parsePackage(PackageParser parser, File packageFile, int flags) {
        if (BuildCompat.isPie()) {
            return PackageParserPie.parsePackage.call(parser, packageFile, flags);
        } else if (API_LEVEL >= M) {
            return PackageParserMarshmallow.parsePackage.call(parser, packageFile, flags);
        } else if (API_LEVEL >= LOLLIPOP_MR1) {
            return PackageParserLollipop22.parsePackage.call(parser, packageFile, flags);
        } else if (API_LEVEL >= LOLLIPOP) {
            return PackageParserLollipop.parsePackage.call(parser, packageFile, flags);
        }
        return black.android.content.pm.PackageParser.parsePackage.call(parser, packageFile, null, new DisplayMetrics(), flags);
    }

    public static void collectCertificates(PackageParser parser, Package p, int flags) {
        if (BuildCompat.isPie()) {
            PackageParserPie.collectCertificates.call(p, true);
        } else if (API_LEVEL >= N) {
            PackageParserNougat.collectCertificates.call(p, flags);
        } else if (API_LEVEL >= M) {
            PackageParserMarshmallow.collectCertificates.call(parser, p, flags);
        } else if (API_LEVEL >= LOLLIPOP_MR1) {
            PackageParserLollipop22.collectCertificates.call(parser, p, flags);
        } else if (API_LEVEL >= LOLLIPOP) {
            PackageParserLollipop.collectCertificates.call(parser, p, flags);
        }
        black.android.content.pm.PackageParser.collectCertificates.call(parser, p, flags);
    }
}
