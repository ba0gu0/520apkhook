package org.a520apkhook;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.apache.commons.io.FileUtils;

public class Config {

    public static String LOGO;
    public static String apkToolFilePath;
    public static String apkSignerFilePath;
    public static String apkKeyStoreFilePath;
    public static String apkBox64FilePath;
    public static String apkBox32FilePath;
    public static String apkBoxApkDecodeDir;
    public static boolean apkBoxUseOldSdk;
    public static String workDir;
    public static Boolean IsJar = false;
    public static HashMap<String, String>apkMetaInfo = null;
    public static String apkIconFilePath;
    public static String apkAdaptiveIconFilePath;
    public static String apkArchType = "armV8";
    public static String hackApkFilePath;
    public static String payloadApkFilePath;
    public static String payloadApkDecodeDir;
    public static String payloadApkApplicationName;
    public static String payloadApkMainActivityName;
    public static String payloadApkMainServiceName;
    public static String payloadApkPackageName;
    public static String payloadApkNewManifestFile;
    public static String payloadApkDexZipFilePath;
    public static String payloadApkDexZipFilePass;
    public static String buildApkFilePath;
    public static Locale LocaleLanguage = Locale.SIMPLIFIED_CHINESE;
    public static String assetsSourceApkFileName;
    public static String assetsClassesDexFileName;
    public static Boolean enableDaemonService = false;
    public static Boolean hideRoot = true;
    public static Boolean hideXposed = true;
    public static String oldPackageName = "com.android.a520apkbox";
    public static String newPackageName = "com.android.a520apkbox";
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
            workDir = System.getProperty("user.dir") + "/workDir";
        }else {
            IsJar = false;
            workDir = System.getProperty("user.dir") + "/workDir";
        }

        apkToolFilePath = workDir + "/libs/apktool.jar";
        apkSignerFilePath = workDir + "/libs/apksigner.jar";
        apkKeyStoreFilePath = workDir + "/libs/Android.keystore";
        apkBox64FilePath = workDir + "/libs/520ApkBox64.apk";
        apkBox32FilePath = workDir + "/libs/520ApkBox32.apk";
        apkBoxApkDecodeDir = workDir + "/apkBoxDecodeDir";
        payloadApkDecodeDir = workDir + "/payloadApkDecodeDir";

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

        if (IsJar){
            LogUtils.info(TAG, "从Jar包中释放工具文件.");
            InputStream apktoolInputStream = Config.class.getResourceAsStream("/jar/resources/apktool.jar");
            InputStream apksignerInputStream = Config.class.getResourceAsStream("/jar/resources/apksigner.jar");
            InputStream apkKeyStoreInputStream = Config.class.getResourceAsStream("/jar/resources/Android.keystore");
            InputStream apkBox64InputStream = Config.class.getResourceAsStream("/jar/resources/520ApkBox64.apk");
            InputStream apkBox32InputStream = Config.class.getResourceAsStream("/jar/resources/520ApkBox32.apk");

            OutputStream apktoolOutputStream = new FileOutputStream(apkToolFilePath);
            OutputStream apksignerOutputStream = new FileOutputStream(apkSignerFilePath);
            OutputStream apkKeyStoreOutputStream = new FileOutputStream(apkKeyStoreFilePath);
            OutputStream apkBox64OutputStream = new FileOutputStream(apkBox64FilePath);
            OutputStream apkBox32OutputStream = new FileOutputStream(apkBox32FilePath);

            int index = 0;// 当前读取的位数
            byte[] bytes = new byte[1024];// 指定每次读取的位数，这里以1024为例
            while ((index = apktoolInputStream.read(bytes)) != -1){
                apktoolOutputStream.write(bytes, 0, index);
            }
            apktoolOutputStream.flush();
            apktoolOutputStream.close();
            apktoolInputStream.close();
            LogUtils.info(TAG, "已释放 apktool.jar .");

            index = 0;// 当前读取的位数
            bytes = new byte[1024];// 指定每次读取的位数，这里以1024为例
            while ((index = apksignerInputStream.read(bytes)) != -1){
                apksignerOutputStream.write(bytes, 0, index);
            }
            apksignerOutputStream.flush();
            apksignerOutputStream.close();
            apksignerInputStream.close();
            LogUtils.info(TAG, "已释放 apksigner.jar .");

            index = 0;// 当前读取的位数
            bytes = new byte[1024];// 指定每次读取的位数，这里以1024为例
            while ((index = apkKeyStoreInputStream.read(bytes)) != -1){
                apkKeyStoreOutputStream.write(bytes, 0, index);
            }
            apkKeyStoreOutputStream.flush();
            apkKeyStoreOutputStream.close();
            apkKeyStoreInputStream.close();
            LogUtils.info(TAG, "已释放 Android.keystore .");

            index = 0;// 当前读取的位数
            bytes = new byte[1024];// 指定每次读取的位数，这里以1024为例
            while ((index = apkBox64InputStream.read(bytes)) != -1){
                apkBox64OutputStream.write(bytes, 0, index);
            }
            apkBox64OutputStream.flush();
            apkBox64OutputStream.close();
            apkBox64InputStream.close();
            LogUtils.info(TAG, "已释放 520ApkBox64.apk .");


            index = 0;// 当前读取的位数
            bytes = new byte[1024];// 指定每次读取的位数，这里以1024为例
            while ((index = apkBox32InputStream.read(bytes)) != -1){
                apkBox32OutputStream.write(bytes, 0, index);
            }
            apkBox32OutputStream.flush();
            apkBox32OutputStream.close();
            apkBox32InputStream.close();
            LogUtils.info(TAG, "已释放 520ApkBox32.apk .");
        }else {
            LogUtils.debug(TAG, "从项目resources目录中文件中释放文件.");
            String homeDir = System.getProperty("user.dir");
            FileUtils.copyFile(new File(homeDir + "/src/main/resources/apktool.jar"), new File(apkToolFilePath));
            LogUtils.info(TAG, "已释放 apktool.jar .");
            FileUtils.copyFile(new File(homeDir + "/src/main/resources/apksigner.jar"), new File(apkSignerFilePath));
            LogUtils.info(TAG, "已释放 apksigner.jar .");
            FileUtils.copyFile(new File(homeDir + "/src/main/resources/Android.keystore"), new File(apkKeyStoreFilePath));
            LogUtils.info(TAG, "已释放 Android.keystore .");
            FileUtils.copyFile(new File(homeDir + "/src/main/resources/520ApkBox64.apk"), new File(apkBox64FilePath));
            LogUtils.info(TAG, "已释放 520ApkBox64.apk .");
            FileUtils.copyFile(new File(homeDir + "/src/main/resources/520ApkBox32.apk"), new File(apkBox32FilePath));
            LogUtils.info(TAG, "已释放 520ApkBox32.apk .");
        }
    }

}
