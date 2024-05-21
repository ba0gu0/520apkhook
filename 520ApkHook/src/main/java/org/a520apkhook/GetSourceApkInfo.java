package org.a520apkhook;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class GetSourceApkInfo
{
    private final ApkFile apkFile;
    private final String TAG = "GetSourceApkInfo";
    public GetSourceApkInfo() throws IOException {
        // 初始化, 打开apk文件
        apkFile = new ApkFile(new File(Config.hackApkFilePath));
    }
    public void closeApkFile(){
        try {
            apkFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.warn(TAG, "无法关闭已打开的apk文件: " + Config.hackApkFilePath);
        }
    }
    public Boolean getApkArchName() {
        try {
            boolean isArmV8 = false;

            // 检查 lib 目录是否存在
            File libDir = new File(Config.hackApkFilePath, "lib");
            if (!libDir.exists() || !libDir.isDirectory()) {
                // 如果 lib 目录不存在，设置为 armV8
                Config.apkArchType = "armV8";
                LogUtils.info(TAG, "lib 目录不存在，设置 Config.apkArchType 为 armV8");
                return true;
            }

            ZipFile apk = new ZipFile(Config.hackApkFilePath);

            // 遍历 APK 文件中的所有 ZipEntries
            Enumeration<?> entries = apk.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String entryName = entry.getName();

                // 检查条目是否在 lib/arm64-v8a 目录下
                if (entryName.startsWith("lib/arm64-v8a/")) {
                    LogUtils.info(TAG, "在注入 Apk 中找到文件: " + entryName);
                    isArmV8 = true;
                    break;
                }
            }

            // 如果该目录下至少有一个文件，则 entry 不为 null
            if (isArmV8) {
                Config.apkArchType = "armV8";
                LogUtils.info(TAG, "获取注入 Apk 的Arch信息: " + "arm64-v8a");
            } else {
                Config.apkArchType = "armV7";
                LogUtils.info(TAG, "获取注入 Apk 的Arch信息: " + "armeabi-v7a");
            }

            // 关闭 APK 文件
            apk.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean getApkMetaInfo() {
        // 获取apk文件的meta数据
        ApkMeta apkMeta;

        try {
            apkFile.setPreferredLocale(Config.LocaleLanguage);
            apkMeta = apkFile.getApkMeta();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        HashMap<String, String> metaInfo = new HashMap<>();

        metaInfo.put("AppName", apkMeta.getLabel());
        metaInfo.put("AppPackageName", apkMeta.getPackageName());
        metaInfo.put("AppVersionName", apkMeta.getVersionName());
        metaInfo.put("AppVersionCode", apkMeta.getVersionCode().toString());
        metaInfo.put("AppMinSdkVersion", apkMeta.getMinSdkVersion());
        metaInfo.put("AppMaxSdkVersion", apkMeta.getMaxSdkVersion());
        metaInfo.put("AppTargetSdkVersion", apkMeta.getTargetSdkVersion());

        LogUtils.info(TAG, "获取注入 Apk 的MetaDate信息: " + metaInfo);
        Config.apkMetaInfo = metaInfo;
        Config.newPackageName = apkMeta.getPackageName() + "abox";
        return true;
    }
    public Boolean getApkIcon () {
        // 获取图标数据
        List<IconFace> apkIcons;

        try {
            apkIcons = apkFile.getAllIcons();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        LogUtils.debug(TAG, apkIcons.toString());

        LogUtils.info(TAG, "获取注入 Apk 的所有图标信息: " + apkIcons.toString());

        int maxDensity = 0;
        IconFace maxIcon = null;
        String maxIconName = "";


        for (IconFace icon : apkIcons) {
            Pattern reGex = Pattern.compile("^Icon.*?(\\.\\w+)',.*?density=(\\d+),");
            Matcher match = reGex.matcher(icon.toString());
            if (match.find()) {
                if (Integer.parseInt(match.group(2)) > maxDensity) {
                    maxDensity = Integer.parseInt(match.group(2));
                    maxIcon = icon;
                    maxIconName = match.group(1);
                }
            }
        }

        if (maxIcon == null){
            return false;
        }

        Objects.requireNonNull(maxIcon);
        LogUtils.info(TAG, "获取注入 Apk 的最大的图标信息: " + maxIcon.toString());

        Config.apkIconFilePath = Config.workDir + "/hackApkIcon" + maxIconName;
        try (FileOutputStream fos = new FileOutputStream(Config.apkIconFilePath);){
            byte[] iconData = maxIcon.getData();
            if (iconData == null || iconData.length == 0) {
                LogUtils.warn(TAG,"图标数据无效或为空");
                return false;
            }
            fos.write(iconData, 0, iconData.length);
            LogUtils.info(TAG, "读取注入 Apk 最大的图标并保存, 保存位置: " + Config.apkIconFilePath);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }

//        int maxAdaptiveDensity = 0;
//        IconFace maxAdaptiveIcon = null;
//        String maxAdaptiveIconName = "";
//
//        for (IconFace icon : apkIcons) {
//            Pattern reGex = Pattern.compile("^AdaptiveIcon.*?(\\.\\w+)',.*?density=(\\d+),");
//            Matcher match = reGex.matcher(icon.toString());
//            if (match.find()) {
//                if (Integer.parseInt(match.group(2)) > maxAdaptiveDensity) {
//                    maxAdaptiveDensity = Integer.parseInt(match.group(2));
//                    maxAdaptiveIcon = icon;
//                    maxAdaptiveIconName = match.group(1);
//                }
//            }
//        }
//        if (maxAdaptiveIcon == null){
//            return true;
//        }
//        LogUtils.info(TAG, "获取注入 Apk 的最大的自适配图标信息: " + maxAdaptiveIcon.toString());

//        Config.apkAdaptiveIconFilePath = Config.workDir + "/hackApkAdaptiveIcon" + maxAdaptiveIconName;
//        try (FileOutputStream fos = new FileOutputStream(Config.apkAdaptiveIconFilePath);){
//            fos.write(maxAdaptiveIcon.getData(), 0, maxAdaptiveIcon.getData().length);
//            LogUtils.info(TAG, "读取注入 Apk 最大的自适配图标并保存, 保存位置: " + Config.apkAdaptiveIconFilePath);
//        }catch (IOException e){
//            e.printStackTrace();
//            return false;
//        }

        return true;
    }

    public Boolean getApkSing(){
        List<ApkV2Signer> apkV2Signers;
        try {
            apkV2Signers = apkFile.getApkV2Singers();
        } catch (Exception e) {
            LogUtils.warn(TAG, "无法获取到apk的签名信息: " + e.getMessage());
            return false;
        }
        LogUtils.info(TAG, apkV2Signers.toString());

        // 检查列表不为空，并且至少有一个元素
        if (!apkV2Signers.isEmpty()) {
            // 获取第一个 ApkV2Signer 对象
            ApkV2Signer signer = apkV2Signers.get(0);

            // 获取证书元数据列表
            List<CertificateMeta> certificateMetas = signer.getCertificateMetas();

            // 检查列表不为空，并且至少有一个元素
            if (!certificateMetas.isEmpty()) {
                // 获取第一个证书元数据对象
                CertificateMeta certificateMeta = certificateMetas.get(0);

                // 获取证书数据
                byte[] certificateData = certificateMeta.getData();

                // 将证书数据转换为 X509Certificate 对象
                X509Certificate certificate = parseCertificate(certificateData);

                // 获取证书的 DN（Distinguished Name）
                String dn = null;
                if (certificate != null) {
                    dn = certificate.getSubjectX500Principal().getName();
                    Config.apkSigners = certificate;
                }else {
                    return false;
                }
                // 输出证书的 DN 信息
                LogUtils.info(TAG, "从APP中提取证书信息 DN: " + dn);
            }
        }
        else {
            return false;
        }
        return true;
    }

    // 解析证书数据为 X509Certificate 对象
    private X509Certificate parseCertificate(byte[] certificateData) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream inputStream = new ByteArrayInputStream(certificateData);
            return (X509Certificate) certificateFactory.generateCertificate(inputStream);
        } catch (CertificateException e) {
            e.printStackTrace();
            return null;
        }
    }
}
