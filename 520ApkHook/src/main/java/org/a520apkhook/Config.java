package org.a520apkhook;

import java.io.*;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.*;

import org.apache.commons.io.FileUtils;

import static org.a520apkhook.CmdUtils.ExtractResource;

public class Config {

    public static String LOGO;
    public static String apkToolFilePath;
    public static String apkSignerFilePath;
    public static String aapt2FilePath;
    public static String zipalignFilePath;
    public static String zipalignLibcFilePath;
    public static String apkKeyStoreFilePath;
    public static String apkBox64FilePath;
    public static String apkBox32FilePath;
    public static String apkBoxApkDecodeDir;
    public static boolean apkBoxUseOldSdk;
    public static String workDir;
    public static Boolean IsJar = false;
    public static ApkMetaInfo apkMetaInfo = null;
    public static String apkIconFilePath;
    public static String apkArchType = "armV8";
    public static String hackApkFilePath;
    public static String payloadApkFilePath;
    public static String byPassPayloadApkFilePath;
    public static String payloadApkDecodeDir;
    public static String payloadApkApplicationName;
    public static String payloadApkMainActivityName;
    public static String payloadApkMainServiceName;
    public static String payloadApkPackageName;
    public static String payloadApkNewManifestFile;
    public static String payloadApkDexZipFilePath;
    public static String payloadApkDexZipFilePass;
    public static String buildApkFilePath;
    public static String zipalignApkFilePath;
    public static String signerApkFilePath;
    public static String assetsSourceApkFileName;
    public static String assetsClassesDexFileName;
    public static Boolean enableDaemonService = false;
    public static Boolean hideRoot = true;
    public static Boolean hideXposed = true;
    public static String oldPackageName = "com.android.a520apkbox";
    public static String newPackageName = "com.android.a520apkbox";
    public static X509Certificate apkSigners = null;
    public static String apkSignerPass = "p@ssw0rd";
    private static final String TAG = "Config";


    static {

        if ("jar".equals(Objects.requireNonNull(Config.class.getResource("")).getProtocol())){
            IsJar = true;
            URL packageUrl = Config.class.getProtectionDomain().getCodeSource().getLocation();
            File file = new File(packageUrl.getPath());
            if(file.isDirectory()){ //如果是目录,指向的是包所在路径，而不是文件所在路径
                workDir = file.getAbsolutePath() + "/workDir"; //直接返回绝对路径
            }else{
                workDir = file.getParent() + "/workDir"; //返回jar所在的父路径
            }
//            workDir = System.getProperty("user.dir") + "/workDir";
        }else {
            IsJar = false;
            workDir = System.getProperty("user.dir") + "/workDir";
        }

        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {  // Windows
            zipalignFilePath = workDir + "/libs/zipalign.exe";
            aapt2FilePath = workDir + "/libs/aapt2.exe";
        } else if (os.contains("mac")) {  // MacOS
            zipalignFilePath = workDir + "/libs/zipalign.Mach-O";
            aapt2FilePath = workDir + "/libs/aapt2.Mach-O";
        } else {  // Linux
            zipalignFilePath = workDir + "/libs/zipalign.ELF";
            aapt2FilePath = workDir + "/libs/aapt2.ELF";
        }

        zipalignLibcFilePath = workDir + "/libs/libc++.so";

        apkToolFilePath = workDir + "/libs/apktool.jar";
        apkSignerFilePath = workDir + "/libs/apksigner.jar";
        apkKeyStoreFilePath = workDir + "/libs/Android.keystore";
        apkBox64FilePath = workDir + "/libs/520ApkBoxSpaceCore64.apk";
        apkBox32FilePath = workDir + "/libs/520ApkBoxSpaceCore32.apk";
        apkBoxApkDecodeDir = workDir + "/apkBoxDecodeDir";
        payloadApkDecodeDir = workDir + "/payloadApkDecodeDir";
        byPassPayloadApkFilePath = workDir + "/ByPassPayloadApk.apk";

        byte[] decodedBytes = Base64.getDecoder().decode("CgogICAgX19fX19fIF9fXyAgIF9fX18gICBfX18gICAgICAgICAgICBfXyAgICBfXyAgX18gICAgICAgICAgICAgICBfXyAgCiAgIC8gX19fXy98X18gXCAvIF9fIFwgLyAgIHwgICBfX19fICAgLyAvX18gLyAvIC8gL19fX18gICBfX19fICAgLyAvX18KICAvX19fIFwgIF9fLyAvLyAvIC8gLy8gL3wgfCAgLyBfXyBcIC8gLy9fLy8gL18vIC8vIF9fIFwgLyBfXyBcIC8gLy9fLwogX19fXy8gLyAvIF9fLy8gL18vIC8vIF9fXyB8IC8gL18vIC8vICw8ICAvIF9fICAvLyAvXy8gLy8gL18vIC8vICw8ICAgCi9fX19fXy8gL19fX18vXF9fX18vL18vICB8X3wvIC5fX18vL18vfF98L18vIC9fLyBcX19fXy8gXF9fX18vL18vfF98ICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgL18vICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAoK");
        LOGO = new String(decodedBytes);

        assetsSourceApkFileName = UUID.randomUUID().toString();
        assetsClassesDexFileName = UUID.randomUUID().toString();

        LogUtils.info(TAG, "创建工作目录, " + workDir);
        File folder = new File(workDir + "/libs");
        if (!folder.exists() && !folder.isDirectory()){
            folder.mkdirs();
        }

        LogUtils.info(TAG, "初始化配置信息完成. ");
    }

    public static void initWorkDir() throws IOException {

//        LogUtils.info(TAG, apkBox64FilePath);
//        System.exit(1);

        String os = System.getProperty("os.name").toLowerCase();

        LogUtils.info(TAG, os);

        String aapt2FileName = new File(aapt2FilePath).getName();

        String apkToolFileName = new File(apkToolFilePath).getName();

        String zipalignFileName = new File(zipalignFilePath).getName();

        String apkSignerFileName = new File(apkSignerFilePath).getName();

        String apkKeyStoreFileName = new File(apkKeyStoreFilePath).getName();

        String apkBox64FileName = new File(apkBox64FilePath).getName();

        String apkBox32FileName = new File(apkBox32FilePath).getName();

        String zipalignLibcFileName = new File(zipalignLibcFilePath).getName();

        if (IsJar){
            LogUtils.info(TAG, "从Jar包中释放工具文件.");
            ExtractResource("/jar/resources/" + apkToolFileName, apkToolFilePath);
            LogUtils.info(TAG, "已释放 apktool.jar .");
            ExtractResource("/jar/resources/" + apkSignerFileName, apkSignerFilePath);
            LogUtils.info(TAG, "已释放 apksigner.jar .");
            ExtractResource("/jar/resources/" + apkKeyStoreFileName, apkKeyStoreFilePath);
            LogUtils.info(TAG, "已释放 Android.keystore .");
            ExtractResource("/jar/resources/aapt/" + aapt2FileName, aapt2FilePath);
            LogUtils.info(TAG, "已释放 aapt .");
            ExtractResource("/jar/resources/zipalign/"+ zipalignFileName, zipalignFilePath);
            LogUtils.info(TAG, "已释放 zipalign .");

            if (os.contains("linux")){
                ExtractResource("/jar/resources/zipalign/"+ zipalignLibcFileName, zipalignLibcFilePath);
                LogUtils.info(TAG, "已释放 zipalign 依赖库 libc++.so .");
            }

            ExtractResource("/jar/resources/ApkBox/" + apkBox32FileName, apkBox32FilePath);
            LogUtils.info(TAG, "已释放 " + apkBox32FileName + " .");
            ExtractResource("/jar/resources/ApkBox/" + apkBox64FileName, apkBox64FilePath);
            LogUtils.info(TAG, "已释放 " + apkBox64FileName + " .");
        }else {
            LogUtils.debug(TAG, "从项目resources目录中文件中释放文件.");
            String homeDir = System.getProperty("user.dir");
            FileUtils.copyFile(new File(homeDir + "/src/main/resources/" + apkToolFileName), new File(apkToolFilePath));
            LogUtils.info(TAG, "已释放 apktool.jar .");
            FileUtils.copyFile(new File(homeDir + "/src/main/resources/" + apkSignerFileName), new File(apkSignerFilePath));
            LogUtils.info(TAG, "已释放 apksigner.jar .");
            FileUtils.copyFile(new File(homeDir + "/src/main/resources/" + apkKeyStoreFileName), new File(apkKeyStoreFilePath));
            LogUtils.info(TAG, "已释放 Android.keystore .");
            FileUtils.copyFile(new File(homeDir + "/src/main/resources/aapt/" + aapt2FileName), new File(aapt2FilePath));
            LogUtils.info(TAG, "已释放 aapt .");
            FileUtils.copyFile(new File(homeDir + "/src/main/resources/zipalign/"+ zipalignFileName), new File(zipalignFilePath));
            LogUtils.info(TAG, "已释放 zipalign .");

            if (os.contains("linux")){
                ExtractResource("/src/main/resources/zipalign/"+ zipalignLibcFileName, zipalignLibcFilePath);
                LogUtils.info(TAG, "已释放 zipalign 依赖库 libc++.so .");
            }

            FileUtils.copyFile(new File(homeDir + "/src/main/resources/ApkBox/" + apkBox64FileName), new File(apkBox64FilePath));
            LogUtils.info(TAG, "已释放 " + apkBox64FileName + " .");
            FileUtils.copyFile(new File(homeDir + "/src/main/resources/ApkBox/" + apkBox32FileName), new File(apkBox32FilePath));
            LogUtils.info(TAG, "已释放 " + apkBox32FileName + " .");

        }
    }

}
