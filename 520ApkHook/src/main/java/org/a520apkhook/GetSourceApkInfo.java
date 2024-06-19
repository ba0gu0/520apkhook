package org.a520apkhook;

import com.ibm.icu.util.ULocale;

import java.io.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;
import static org.a520apkhook.CmdUtils.RunCommand;

public class GetSourceApkInfo
{
    private static final String TAG = "GetSourceApkInfo";

    public Boolean getApkArchName() {
        try {
            boolean isArmV8 = false;
            boolean libDirExists = false;

            // 使用 ZipFile 检查 APK 文件的内容
            try (ZipFile apk = new ZipFile(Config.hackApkFilePath)) {
                Enumeration<? extends ZipEntry> entries = apk.entries();

                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String entryName = entry.getName();

                    // 检查是否存在 lib 目录
                    if (entryName.startsWith("lib/")) {
                        libDirExists = true;
                    }

                    // 检查条目是否在 lib/arm64-v8a 目录下
                    if (entryName.startsWith("lib/arm64-v8a/")) {
                        LogUtils.info(TAG, "在注入 Apk 中找到文件: " + entryName);
                        isArmV8 = true;
                        break;
                    }
                }
            }

            // 根据 lib 目录的存在性和文件路径判断架构类型
            if (!libDirExists) {
                Config.apkArchType = "armV8";
                LogUtils.info(TAG, "lib 目录不存在，设置 Config.apkArchType 为 armV8");
            } else if (isArmV8) {
                Config.apkArchType = "armV8";
                LogUtils.info(TAG, "获取注入 Apk 的Arch信息: " + "arm64-v8a");
            } else {
                Config.apkArchType = "armV7";
                LogUtils.info(TAG, "获取注入 Apk 的Arch信息: " + "armeabi-v7a");
            }
        } catch (IOException e) {
            LogUtils.warn(TAG, "无法打开 HackApk 文件: " + e.getMessage());
            return false;
        }
        return true;
    }

    public Boolean getApkMetaInfo() {
        ApkMetaInfo metaInfo = new ApkMetaInfo();

        // 初始化aapt2命令输出
        StringBuilder output = new StringBuilder();

        if (RunCommand(new File(Config.aapt2FilePath), new String[]{"dump", "badging", Config.hackApkFilePath}, output)) {
            String outputStr = output.toString();
            metaInfo.setAppPackageName(extractValue(outputStr, "package: name='"));
            metaInfo.setAppVersionName(extractValue(outputStr, "versionName='"));
            metaInfo.setAppVersionCode(extractValue(outputStr, "versionCode='"));
            metaInfo.setAppMinSdkVersion(extractValue(outputStr, "sdkVersion:'"));
            metaInfo.setAppTargetSdkVersion(extractValue(outputStr, "targetSdkVersion:'"));

            // 获取应用名称并存储到metaInfo中
            List<ApkMetaInfo.AppName> appNames = extractAppName(outputStr);
            if (appNames.isEmpty()){
                return false;
            }
            metaInfo.setAppNames(appNames);
        } else {
            return false;
        }

        LogUtils.info(TAG, "获取注入 Apk 的MetaDate信息: " + metaInfo);
        Config.apkMetaInfo = metaInfo;
        Config.newPackageName = metaInfo.getAppPackageName() + ".box";
        return true;
    }

    private static String extractValue(String output, String startToken) {
        int startIndex = output.indexOf(startToken);
        if (startIndex != -1) {
            startIndex += startToken.length();
            int endIndex = output.indexOf("'", startIndex);
            if (endIndex != -1) {
                return output.substring(startIndex, endIndex);
            }
        }
        return null;
    }


    private static List<ApkMetaInfo.AppName> extractAppName(String output) {
        List<ApkMetaInfo.AppName> appNames = new ArrayList<>();
        String baseLabel = "application-label";
        int index = 0;

        while ((index = output.indexOf(baseLabel, index)) != -1) {
            int startIndex = output.indexOf("'", index) + 1;
            int endIndex = output.indexOf("'", startIndex);
            if (startIndex > 0 && endIndex > startIndex) {

                String language;
                String name = output.substring(startIndex, endIndex);

                if (index + baseLabel.length() == output.indexOf(":", index)){
                    language = "default";
                }else {
                    language = output.substring(index + baseLabel.length() + 1, output.indexOf(":", index)).trim();
                    language = processLanguageTag(language);
                }
                appNames.add(new ApkMetaInfo.AppName(language, name));
            }
            index = endIndex + 1;
        }

        return appNames;
    }

    public static String processLanguageTag(String languageTag) {
        ULocale locale = ULocale.forLanguageTag(languageTag);
        String language = locale.getLanguage();
        String script = locale.getScript();
        String region = locale.getCountry();

        // 使用 ULocale.Builder 构建自定义格式的语言标签
        StringBuilder processedTag = new StringBuilder();

        if (!script.isEmpty() && !region.isEmpty()) {
            processedTag.append("b+").append(language).append("+").append(script).append("+").append(region);
        } else if (!script.isEmpty()) {
            processedTag.append("b+").append(language).append("+").append(script);
        } else if (!region.isEmpty()) {
            if ("419".equals(region)) {
                processedTag.append("b+").append(language).append("+419");
            } else {
                processedTag.append(language).append("-r").append(region);
            }
        } else {
            processedTag.append(language);
        }

        return processedTag.toString();
    }

    public Boolean getApkIcon() {
        Config.apkIconFilePath = Config.workDir + "/hackApkIcon";

        StringBuilder output = new StringBuilder();

        // Step 1: Extract icon resource ID from AndroidManifest.xml
        if (RunCommand(new File(Config.aapt2FilePath), new String[]{"dump", "xmltree", "--file", "AndroidManifest.xml", Config.hackApkFilePath}, output)) {
            String iconResourceId = extractIconResourceId(output.toString());
            LogUtils.info(TAG, "获取到图标的Resource ID: " + iconResourceId);

            if (iconResourceId == null) {
                return false;
            } else {
                output = new StringBuilder();
                // Step 2: Get icon file path using resource ID
                if (RunCommand(new File(Config.aapt2FilePath), new String[]{"dump", "resources", Config.hackApkFilePath}, output)) {
                    List<String> iconFilePaths = extractIconFilePaths(output.toString(), iconResourceId);

                    LogUtils.info(TAG, "获取到所有图标的位置信息: " + iconFilePaths.toString());

                    if (iconFilePaths.isEmpty()) {
                        return false;
                    } else {
                        // 找到大小最大的文件路径
                        String largestIconFilePath = null;
                        long largestFileSize = -1;

                        for (String path : iconFilePaths) {
                            long fileSize = getFileSizeInApk(Config.hackApkFilePath, path);
                            if (fileSize > largestFileSize) {
                                largestFileSize = fileSize;
                                largestIconFilePath = path;
                            }
                        }

                        LogUtils.info(TAG, "获取到最大的图标文件为: " + largestIconFilePath);

                        if (largestIconFilePath != null) {
                            String tempIconPath = Config.apkIconFilePath + ".tmp";
                            if (extractFileFromApk(Config.hackApkFilePath, largestIconFilePath, tempIconPath)) {
                                LogUtils.info(TAG, "解压app的图标到: " + tempIconPath);

                                // Step 4: Determine file type and rename accordingly
                                String fileType = getImageType(tempIconPath);
                                if (fileType != null) {
                                    String newIconPath = Config.apkIconFilePath + "." + fileType;
                                    File tempIconFile = new File(tempIconPath);
                                    File newIconFile = new File(newIconPath);

                                    // 删除目标文件（如果存在）
                                    if (newIconFile.exists()) {
                                        if (!newIconFile.delete()) {
                                            LogUtils.warn(TAG, "无法删除已有的图标文件: " + newIconPath);
                                            return false;
                                        }
                                    }

                                    // 重命名文件
                                    if (tempIconFile.renameTo(newIconFile)) {
                                        Config.apkIconFilePath = newIconPath;
                                        LogUtils.info(TAG, "图标文件重命名为: " + newIconPath);
                                        return true;
                                    } else {
                                        LogUtils.warn(TAG, "重命名图标文件失败");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private long getFileSizeInApk(String apkFilePath, String filePathInApk) {
        try (ZipFile zipFile = new ZipFile(apkFilePath)) {
            ZipEntry entry = zipFile.getEntry(filePathInApk);
            if (entry != null) {
                return entry.getSize();
            }
        } catch (IOException e) {
            LogUtils.warn(TAG, "无法读取APK中文件大小: " + e.getMessage());
        }
        return -1;
    }

    private String getImageType(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] header = new byte[12];
            if (fis.read(header) == 12) {
                if (header[0] == (byte) 0x89 && header[1] == (byte) 0x50 && header[2] == (byte) 0x4E && header[3] == (byte) 0x47) {
                    return "png";
                } else if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) {
                    return "jpg";
                } else if (header[0] == (byte) 0x47 && header[1] == (byte) 0x49 && header[2] == (byte) 0x46) {
                    return "gif";
                } else if (header[0] == (byte) 0x52 && header[1] == (byte) 0x49 && header[2] == (byte) 0x46 && header[3] == (byte) 0x46) {
                    return "webp";
                } else if (header[0] == (byte) 0x42 && header[1] == (byte) 0x4D) {
                    return "bmp";
                } else if (header[0] == (byte) 0x49 && header[1] == (byte) 0x49 && header[2] == (byte) 0x2A && header[3] == (byte) 0x00) {
                    return "tiff";
                } else if (header[0] == (byte) 0x4D && header[1] == (byte) 0x4D && header[2] == (byte) 0x00 && header[3] == (byte) 0x2A) {
                    return "tiff";
                } else if (header[0] == (byte) 0x00 && header[1] == (byte) 0x00 && header[2] == (byte) 0x01 && header[3] == (byte) 0x00) {
                    return "ico";
                }
            }
        } catch (IOException e) {
            LogUtils.warn(TAG, "无法读取文件头: " + e.getMessage());
        }
        return null;
    }

    private String extractIconResourceId(String manifestContent) {
        Pattern pattern = Pattern.compile("android:icon\\(0x[0-9a-f]+\\)=@0x([0-9a-f]+)");
        Matcher matcher = pattern.matcher(manifestContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractIconFilePath(String resourcesContent, String iconResourceId) {
        // 创建正则表达式来匹配两种格式的资源路径
        Pattern pattern = Pattern.compile(
            "resource 0x" + iconResourceId + "[^\\n]*\\n\\s*\\([^\\)]+\\) \\(file\\) ([^\\s]+) type=(PNG|JPG|GIF|WEBP)|" +
            "resource 0x" + iconResourceId + "[^\\n]*\\n\\s*\\([^\\)]+\\) \"([^\"]+)\""
        );
        Matcher matcher = pattern.matcher(resourcesContent);
        if (matcher.find()) {
            // 检查哪个捕获组匹配到了路径
            if (matcher.group(1) != null) {
                return matcher.group(1);
            } else if (matcher.group(3) != null) {
                return matcher.group(3);
            }
        }
        return null;
    }


    private List<String> extractIconFilePaths(String resourcesContent, String iconResourceId) {
        List<String> filePaths = new ArrayList<>();

        // 创建正则表达式来匹配资源块，从指定的 resource 0x 开头，匹配到下一个 resource 0x
        Pattern blockPattern = Pattern.compile(
            "resource 0x" + iconResourceId + ".*?(?=resource 0x|$)",
            Pattern.DOTALL
        );

        Matcher blockMatcher = blockPattern.matcher(resourcesContent);

        while (blockMatcher.find()) {
            // 从匹配到的块中提取所有路径
            String block = blockMatcher.group();

//            LogUtils.debug(TAG,"Block matched: \n" + block); // Debugging output

            // 创建正则表达式来匹配文件路径
            Pattern filePattern = Pattern.compile(
                "[^\\n]*\\n\\s*\\([^\\)]+\\) \\(file\\) ([^\\s]+) type=(PNG|JPG|GIF|WEBP)|" +
                "[^\\n]*\\n\\s*\\([^\\)]+\\) \"([^\"]+\\.(png|jpg|gif|webp))\"",
                Pattern.CASE_INSENSITIVE
            );
            Matcher fileMatcher = filePattern.matcher(block);

            while (fileMatcher.find()) {
                // 检查哪个捕获组匹配到了路径
                if (fileMatcher.group(1) != null) {
                    filePaths.add(fileMatcher.group(1));
                } else if (fileMatcher.group(3) != null) {
                    filePaths.add(fileMatcher.group(3));
                }
            }
        }

        return filePaths;
    }

    private boolean extractFileFromApk(String apkFilePath, String filePath, String outputFilePath) {
        try (ZipFile zipFile = new ZipFile(apkFilePath)) {
            ZipEntry entry = zipFile.getEntry(filePath);
            if (entry != null) {
                try (InputStream inputStream = zipFile.getInputStream(entry);
                     OutputStream outputStream = new FileOutputStream(outputFilePath)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    return true;
                }
            }
        } catch (IOException e) {
            LogUtils.warn(TAG, "解压文件 " + filePath + ", 失败: " + e.getMessage());
            return false;
        }
        return false;
    }

    public Boolean getApkSigner() {

        // 从 APK 文件中提取证书
        X509Certificate certificate = extractCertificateFromAPK(Config.hackApkFilePath);
        if (certificate != null) {
            String dn = certificate.getSubjectX500Principal().getName();
            Config.apkSigners = certificate;
            LogUtils.info(TAG, "从APP中提取证书信息 DN: " + dn);
            return true;
        }

        return false;
    }

    private static X509Certificate extractCertificateFromAPK(String apkFilePath) {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        File apkFile = new File(apkFilePath);
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(apkFile)))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().startsWith("META-INF/") && entry.getName().endsWith(".RSA")) {
                    // 使用CertificateFactory从ZipInputStream生成证书
                    CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");
                    return (X509Certificate) certFactory.generateCertificate(zis);
                }
            }
        } catch (IOException | CertificateException | NoSuchProviderException e) {
            LogUtils.warn(TAG, "获取 HackApk 的证书信息失败: " + e.getMessage());
        }
        return null;
    }

}
