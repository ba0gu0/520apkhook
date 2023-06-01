package org.a520apkhook;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import net.dongliu.apk.parser.bean.IconFace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetSourceApkInfo
{
    private final ApkFile apkFile;
    private final String TAG = "GetSourceApkInfo";
    public GetSourceApkInfo() throws IOException {
        // 初始化, 打开apk文件
        apkFile = new ApkFile(new File(Config.hackApkFilePath));
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
        LogUtils.info(TAG, "获取注入 Apk 的图标信息: " + maxIcon.toString());

        Config.apkIconFilePath = Config.workDir + "/hackApkIcon" + maxIconName;
        try (FileOutputStream fos = new FileOutputStream(Config.apkIconFilePath);){
            fos.write(maxIcon.getData(), 0, maxIcon.getData().length);
            LogUtils.info(TAG, "读取注入 Apk 图标并保存, 保存位置: " + Config.apkIconFilePath);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
