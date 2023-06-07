package org.a520apkhook;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import net.dongliu.apk.parser.bean.IconFace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
    public Boolean getApkArchName(){
        try {
            boolean isArmV8 = false;
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
        Config.newPackageName = apkMeta.getPackageName() + ".a520apkbox";
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
        Objects.requireNonNull(maxIcon);
        LogUtils.info(TAG, "获取注入 Apk 的最大的图标信息: " + maxIcon.toString());

        Config.apkIconFilePath = Config.workDir + "/hackApkIcon" + maxIconName;
        try (FileOutputStream fos = new FileOutputStream(Config.apkIconFilePath);){
            fos.write(maxIcon.getData(), 0, maxIcon.getData().length);
            LogUtils.info(TAG, "读取注入 Apk 最大的图标并保存, 保存位置: " + Config.apkIconFilePath);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }

        int maxAdaptiveDensity = 0;
        IconFace maxAdaptiveIcon = null;
        String maxAdaptiveIconName = "";

        for (IconFace icon : apkIcons) {
            Pattern reGex = Pattern.compile("^AdaptiveIcon.*?(\\.\\w+)',.*?density=(\\d+),");
            Matcher match = reGex.matcher(icon.toString());
            if (match.find()) {
                if (Integer.parseInt(match.group(2)) > maxAdaptiveDensity) {
                    maxAdaptiveDensity = Integer.parseInt(match.group(2));
                    maxAdaptiveIcon = icon;
                    maxAdaptiveIconName = match.group(1);
                }
            }
        }
        if (maxAdaptiveIcon == null){
            return true;
        }
        LogUtils.info(TAG, "获取注入 Apk 的最大的自适配图标信息: " + maxAdaptiveIcon.toString());

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
}
