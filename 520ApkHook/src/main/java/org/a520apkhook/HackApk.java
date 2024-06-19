package org.a520apkhook;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.a520apkhook.CmdUtils.*;

public class HackApk {
    private static final String TAG = "HackApk";
    private static final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    HackApk () {
        // 初始化, 打开apk文件
    }
    public Boolean decodeApkFile (){
        if (Objects.equals(Config.apkArchType, "armV8")){
            LogUtils.info(TAG, "目标Apk支持64位, 使用apktool反编译模板Apk, " + Config.apkBox64FilePath);
            return RunCommand(new File(Config.apkToolFilePath), new String[]{"d", Config.apkBox64FilePath, "-f", "-o", Config.apkBoxApkDecodeDir});
        }else {
            LogUtils.info(TAG, "目标Apk仅支持32位, 使用apktool反编译模板Apk, " + Config.apkBox32FilePath);
            return RunCommand(new File(Config.apkToolFilePath), new String[]{"d", Config.apkBox32FilePath, "-f", "-o", Config.apkBoxApkDecodeDir});
        }

    }

    public Boolean generateKeyStore() {
        try {
            if (Config.apkSigners == null) {
                LogUtils.warn(TAG,"无法从APP中提取组织信息，使用自带的证书文件: " + Config.apkKeyStoreFilePath);
                return true;
            }

            Config.apkSignerPass = generateRandomPassword(8);

            // 添加BouncyCastle提供者
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            // 生成密钥对
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // 生成证书请求
            X500Name subject = new X500Name(Config.apkSigners.getSubjectX500Principal().getName());
            PKCS10CertificationRequest csr = new JcaPKCS10CertificationRequestBuilder(subject, keyPair.getPublic())
                    .build(new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate()));

            // 获取证书信息
            X500Name issuer = new X500Name(Config.apkSigners.getIssuerX500Principal().getName());
            Date notBefore = Config.apkSigners.getNotBefore();
            Date notAfter = Config.apkSigners.getNotAfter();
            BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

            // 生成新证书
            X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    issuer,
                    serial,
                    notBefore,
                    notAfter,
                    subject,
                    csr.getSubjectPublicKeyInfo()
            );

            ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
            X509CertificateHolder certHolder = certBuilder.build(contentSigner);
            X509Certificate newCert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

            // 保存证书到密钥库中
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(null, null);
            keyStore.setKeyEntry("Android", keyPair.getPrivate(), Config.apkSignerPass.toCharArray(), new X509Certificate[]{newCert});

            // 将 keystore 保存到文件中
            try (FileOutputStream fos = new FileOutputStream(Config.apkKeyStoreFilePath)) {
                keyStore.store(fos, Config.apkSignerPass.toCharArray());
            }

            LogUtils.info(TAG, "使用获取到的组织信息，生成新的证书文件。新的证书文件保存在: " + Config.apkKeyStoreFilePath + " .  证书密码为: " + Config.apkSignerPass);
        } catch (Exception e) {
            LogUtils.warn(TAG, "无法生成新的证书: " + e.getMessage());
            return false;
        }
        return true;
    }

    private static String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            // 生成随机索引，从 ALLOWED_CHARACTERS 中获取字符
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
            password.append(randomChar);
        }
        return password.toString();
    }

    public Boolean changeAndroidManifest() {
        LogUtils.info(TAG, "解析并修改模板Apk的AndroidManifest.xml文件. ");
        File xmlFile = new File(Config.apkBoxApkDecodeDir + "/AndroidManifest.xml");
        Document document = readXmlFile(xmlFile);
        if (document == null){
            return false;
        }
        Element rootElement = document.getRootElement();

        // 设置 apkbox 中相关的配置项。
        List<Element> metaDataElements = rootElement.element("application").elements("meta-data");
        for (Element metaDataElement : metaDataElements){
            if(metaDataElement.attribute("name").getValue().equals("HackAppFileName")){
               metaDataElement.addAttribute("value", Config.assetsSourceApkFileName);
                LogUtils.info(TAG, "设置模板Appassets目录存储的被注入Apk名字. " + Config.assetsSourceApkFileName);
            }

            if(metaDataElement.attribute("name").getValue().equals("HackAppPackageName")){
               metaDataElement.addAttribute("value", Config.apkMetaInfo.getAppPackageName());
                LogUtils.info(TAG, "设置包装器的启动包名. " + Config.apkMetaInfo.getAppPackageName());
            }

            if(metaDataElement.attribute("name").getValue().equals("EnableDaemonService")){
               metaDataElement.addAttribute("value", Config.enableDaemonService.toString());
                LogUtils.info(TAG, "设置包装器开启守护进程. " + Config.enableDaemonService.toString());
            }

            if(metaDataElement.attribute("name").getValue().equals("HideRoot")){
               metaDataElement.addAttribute("value", Config.hideRoot.toString());
                LogUtils.info(TAG, "设置包装器隐藏Root. " + Config.hideRoot.toString());
            }

            if(metaDataElement.attribute("name").getValue().equals("HideXposed")){
               metaDataElement.addAttribute("value", Config.hideXposed.toString());
               LogUtils.info(TAG, "设置包装器隐藏Xposed. " + Config.hideXposed.toString());
            }

            if(metaDataElement.attribute("name").getValue().equals("DexZipFileName")){
               metaDataElement.addAttribute("value", Config.assetsClassesDexFileName);
               LogUtils.info(TAG, "设置压缩后的dex文件名字. " + Config.assetsClassesDexFileName);
            }

            if(metaDataElement.attribute("name").getValue().equals("DexZipFilePass")){
               metaDataElement.addAttribute("value", Config.payloadApkDexZipFilePass);
               LogUtils.info(TAG, "设置dex文件的压缩包密码. " + Config.payloadApkDexZipFilePass);
            }

            if(metaDataElement.attribute("name").getValue().equals("RealApplicationName")){
                if (Config.payloadApkApplicationName != null){
                    metaDataElement.addAttribute("value", Config.payloadApkApplicationName);
                    LogUtils.info(TAG, "设置 Payload apk 的ApplicationName. " + Config.payloadApkApplicationName);
                }else {
                    metaDataElement.addAttribute("value", "android.app.Application");
                    LogUtils.info(TAG, "设置 Payload apk 的ApplicationName. android.app.Application");
                }
            }
            if (Config.payloadApkMainServiceName == null){
                if(metaDataElement.attribute("name").getValue().equals("RealActivityName")){
                    metaDataElement.addAttribute("value", Config.payloadApkMainActivityName);
                    LogUtils.info(TAG, "设置 Payload apk 的MainActivityName. " + Config.payloadApkMainActivityName);
                }
            }

            if (Config.payloadApkMainServiceName != null){
                if(metaDataElement.attribute("name").getValue().equals("RealServiceName")){
                    metaDataElement.addAttribute("value", Config.payloadApkMainServiceName);
                    LogUtils.info(TAG, "设置 Payload apk 的 RealServiceName. " + Config.payloadApkMainServiceName);
                }
            }

            LogUtils.debug(TAG, metaDataElement.toString());
        }

        // 读取payload apk中的信息，追加到 apkbox 中
        LogUtils.info(TAG, "将payload apk中的 AndroidManifest-new.xml 追加到apkbox的 AndroidManifest.xml中.");
        Document payloadApkDocument = readXmlFile(new File(Config.payloadApkNewManifestFile));
        Element payloadApkRootElement = payloadApkDocument.getRootElement();

        for (Element tempElement : payloadApkRootElement.elements()){
            rootElement.element("application").add(tempElement.createCopy());
        }

        LogUtils.debug(TAG, rootElement.element("application").toString());

        LogUtils.debug(TAG, rootElement.element("application").elements("meta-data").toString());

        if (!writeXmlFile(document, xmlFile)){
            return false;
        };

        return true;
    }

    public boolean setApkBoxAppName() {
        LogUtils.info(TAG, "解析并修改模板App的string.xml文件.");

        for (ApkMetaInfo.AppName appName : Config.apkMetaInfo.getAppNames()) {
            if ("default".equals(appName.getLanguage())) {
                File xmlFile = new File(Config.apkBoxApkDecodeDir + "/res/values/strings.xml");
                updateAppNameInXml(xmlFile, appName.getName());
                LogUtils.info(TAG, "设置模板App默认显示的名字: " + appName.getName());
            } else {
                String language = appName.getLanguage();
                File languageDir = new File(Config.apkBoxApkDecodeDir + "/res/values-" + language);
                if (!languageDir.exists()) {
                    if (!languageDir.mkdirs()) {
                        LogUtils.warn(TAG, "创建目录失败: " + languageDir.getPath());
                        return false;
                    }
                }

                File xmlFile = new File(languageDir, "strings.xml");
                if (!xmlFile.exists()) {
                    try {
                        if (!xmlFile.createNewFile()) {
                            LogUtils.warn(TAG, "创建文件失败: " + xmlFile.getPath());
                            return false;
                        }
                        // 初始化空的 XML 文件
                        Document document = org.dom4j.DocumentHelper.createDocument();
                        document.addElement("resources");
                        writeXmlFile(document, xmlFile);
                    } catch (IOException e) {
                        LogUtils.warn(TAG, "创建文件失败: " + e.getMessage());
                        return false;
                    }
                }

                updateAppNameInXml(xmlFile, appName.getName());
                LogUtils.info(TAG, "设置 " + language + " 语言的App显示的名字: " + appName.getName());
            }
        }

        return true;
    }
    public static void updateAppNameInXml(File xmlFile, String appName) {
        Document document = readXmlFile(xmlFile);
        if (document == null) {
            return;
        }

        Element rootElement = document.getRootElement();
        List<Element> stringElements = rootElement.elements("string");
        boolean found = false;

        for (Element stringElement : stringElements) {
            if (stringElement.attribute("name").getValue().equals("app_name")) {
                stringElement.setText(appName);
                found = true;
                break;
            }
        }

        if (!found) {
            Element newStringElement = rootElement.addElement("string");
            newStringElement.addAttribute("name", "app_name");
            newStringElement.setText(appName);
        }

        writeXmlFile(document, xmlFile);
    }
    public boolean fixValuesV31Colors(){
        LogUtils.info(TAG, "修复模板App的values-v31/colors.xml文件. ");

        Path colorXmlPath = Paths.get(Config.apkBoxApkDecodeDir + "/res/values-v31/colors.xml");

        try {
            ReplaceFileContent(colorXmlPath, "@android", "@*android");
        }catch (IOException e) {
            LogUtils.info(TAG, "修复模板App的values-v31/colors.xml文件失败. ");
            return false;
        }
        LogUtils.info(TAG, "修复模板App的values-v31/colors.xml文件成功. ");

        return  true;
    }

    public void changeApktoolYaml() throws IOException {
        LogUtils.info(TAG, "解析并修改模板App的apktool.yml文件. ");

        Path yamlPath = Paths.get(Config.apkBoxApkDecodeDir + "/apktool.yml");

        if (Config.apkBoxUseOldSdk){
            ReplaceFileContent(yamlPath, "minSdkVersion: \\d+", String.format("minSdkVersion: %s", 16));
            ReplaceFileContent(yamlPath, "targetSdkVersion: \\d+", String.format("targetSdkVersion: %s", 22));
        }else {
            ReplaceFileContent(yamlPath, "minSdkVersion: \\d+", String.format("minSdkVersion: %s", Config.apkMetaInfo.getAppMinSdkVersion()));
            ReplaceFileContent(yamlPath, "targetSdkVersion: \\d+", String.format("targetSdkVersion: %s", Config.apkMetaInfo.getAppTargetSdkVersion()));
        }
        ReplaceFileContent(yamlPath, "versionName: [\\d.]+", String.format("versionName: %s", Config.apkMetaInfo.getAppVersionName()));
        ReplaceFileContent(yamlPath, "versionCode: [\\d.]+", String.format("versionCode: %s", Config.apkMetaInfo.getAppVersionCode()));

        LogUtils.info(TAG, String.format("修改模板App的minSdkVersion: %s、targetSdkVersion: %s、versionName: %s、versionCode: %s 信息. ", Config.apkMetaInfo.getAppMinSdkVersion(), Config.apkMetaInfo.getAppTargetSdkVersion(), Config.apkMetaInfo.getAppVersionName(), Config.apkMetaInfo.getAppVersionCode()));

        LogUtils.info(TAG, "修改模板App的apktool.yml文件成功. ");

    }

    public void changeApkBoxPackageName() throws IOException {

        if (Objects.equals(Config.oldPackageName, Config.newPackageName)) {
            LogUtils.info(TAG, "PackageName没有发生变化, 直接返回.");
            return;
        }

        String oldSmaliPackageName = Config.oldPackageName.replaceAll("\\.", "/");
        String oldSmaliPath = Config.apkBoxApkDecodeDir + "/smali/" + Config.oldPackageName.replaceAll("\\.", "/");
        LogUtils.info(TAG, "原始smali文件路径: " + oldSmaliPath);

        String newSmaliPackageName = Config.newPackageName.replaceAll("\\.", "/");
        String newSmaliPath = Config.apkBoxApkDecodeDir + "/smali/" + Config.newPackageName.replaceAll("\\.", "/");
        LogUtils.info(TAG, "修改后smali文件路径: " + newSmaliPath);

        File oldFileDir = new File(oldSmaliPath);
        File newFileDir = new File(newSmaliPath);

//        LogUtils.debug(TAG, String.valueOf(newFileDir.exists()));

        if (!newFileDir.exists()){
            if(newFileDir.mkdirs()){
                LogUtils.info(TAG, "成功创建新的smali目录: " + newSmaliPath);
            }else {
                LogUtils.warn(TAG, "目录创建失败: " + newSmaliPath);
                throw new IOException("目录创建失败: " + newSmaliPath);
            }
        }

        FileUtils.copyDirectory(oldFileDir, newFileDir);
        FileUtils.deleteDirectory(oldFileDir);
        LogUtils.info(TAG, "复制原始smali文件到新的smali目录中.");

        List<File> fileList = (List<File>) FileUtils.listFiles(newFileDir,null,false);
        for (File file : fileList){
            ReplaceFileContent(Paths.get(file.toString()), oldSmaliPackageName, newSmaliPackageName);
            ReplaceFileContent(Paths.get(file.toString()), Config.oldPackageName, Config.newPackageName);
            LogUtils.info(TAG, String.format("已修改smali文件: %s, 替换 %s 为 %s , 替换 %s 为 %s .", file.toString(), oldSmaliPackageName, newSmaliPackageName, Config.oldPackageName, Config.newPackageName));
        }
        ReplaceFileContent(Paths.get(Config.apkBoxApkDecodeDir + "/AndroidManifest.xml"), Config.oldPackageName, Config.newPackageName);
        LogUtils.info(TAG, String.format("已修改AndroidManifest.xml文件. 替换 %s 为 %s ", Config.oldPackageName, Config.newPackageName));
    }

    public void copyAssesResFile () throws IOException {
        LogUtils.info(TAG, "开始复制资源文件到模板App中.");

        FileUtils.copyFile(new File(Config.payloadApkDexZipFilePath), new File(Config.apkBoxApkDecodeDir + String.format("/assets/%s", Config.assetsClassesDexFileName)));
        LogUtils.info(TAG, "已将Payload Apk 的Dex压缩包复制到模板App的assets目录中.");

        FileUtils.copyFile(new File(Config.hackApkFilePath), new File(Config.apkBoxApkDecodeDir + String.format("/assets/%s", Config.assetsSourceApkFileName)));
        LogUtils.info(TAG, "已将被注入Apk复制到模板App的assets目录中.");

        if (Config.apkIconFilePath != null && new File(Config.apkIconFilePath).exists()){
            FileUtils.delete(new File(Config.apkBoxApkDecodeDir + "/res/mipmap-xxxhdpi/ic_launcher.png"));
            LogUtils.info(TAG, "已清空模板App中的 mipmap-xxxhdpi 文件夹.");
            FileUtils.copyFile(new File(Config.apkIconFilePath), new File(Config.apkBoxApkDecodeDir + "/res/mipmap-xxxhdpi/ic_launcher." + FilenameUtils.getExtension(Config.apkIconFilePath)));

            LogUtils.info(TAG, "重新向模板App中的 mipmap-xxxhdpi 文件夹复制图标文件.");
        }

        if (new File(Config.payloadApkDecodeDir + "/res/xml").exists()){
            FileUtils.copyDirectory(new File(Config.payloadApkDecodeDir + "/res/xml"), new File(Config.apkBoxApkDecodeDir + "/res/xml/"));
            LogUtils.info(TAG, "已复制Payload apk中的res/xml目录到520ApkBox中.");
        }

        LogUtils.info(TAG, "所有资源文件已复制完成.");
    }

    public Boolean buildApkFile(){
        LogUtils.info(TAG, "正在进行重新编译模板App.");
        Config.buildApkFilePath = System.getProperty("user.dir") + "/520ApkHook.apk";

        return RunCommand(new File(Config.apkToolFilePath), new String[]{"b", "-f", "-o", Config.buildApkFilePath, Config.apkBoxApkDecodeDir});
    }

    public Boolean zipalignApkFile(){
        LogUtils.info(TAG, "正在进行重新编译模板App.");
        Config.zipalignApkFilePath = System.getProperty("user.dir") + "/520ApkHook.zipalign.apk";

        return RunCommand(new File(Config.zipalignFilePath), new String[]{"-f", "-p" ,"4", Config.buildApkFilePath, Config.zipalignApkFilePath});
    }

    public Boolean signerApk(){
        LogUtils.info(TAG, "正在对模板App重新进行签名.");

        Config.signerApkFilePath = System.getProperty("user.dir") + "/520ApkHook.zipalign.signer.apk";

        return RunCommand(new File(Config.apkSignerFilePath), new String[]{"sign", "--v1-signing-enabled", "true", "--v2-signing-enabled", "true", "--v3-signing-enabled", "true", "--v4-signing-enabled", "true", "--ks", Config.apkKeyStoreFilePath, "--ks-pass", "pass:" + Config.apkSignerPass, "--ks-key-alias", "Android", "--key-pass", "pass:" + Config.apkSignerPass, "--in", Config.zipalignApkFilePath, "--out", Config.signerApkFilePath});
    }

}
