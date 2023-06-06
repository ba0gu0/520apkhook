package org.a520apkhook;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class HackApk {
    private final String TAG = "HackApk";
    HackApk () {
        // 初始化, 打开apk文件
    }
    public Boolean decodeApkFile (){
        if (Objects.equals(Config.apkArchType, "armV8")){
            LogUtils.info(TAG, "目标Apk支持64位, 使用apktool反编译模板Apk, " + Config.apkBox64FilePath);
            return runJar(new File(Config.apkToolFilePath), new String[]{"d", Config.apkBox64FilePath, "-f", "-o", Config.apkBoxApkDecodeDir});
        }else {
            LogUtils.info(TAG, "目标Apk仅支持32位, 使用apktool反编译模板Apk, " + Config.apkBox32FilePath);
            return runJar(new File(Config.apkToolFilePath), new String[]{"d", Config.apkBox32FilePath, "-f", "-o", Config.apkBoxApkDecodeDir});
        }

    }

    public Boolean changeAndroidManifest() {
        LogUtils.info(TAG, "解析并修改模板Apk的AndroidManifest.xml文件. ");
        File xmlFile = new File(Config.apkBoxApkDecodeDir + "/AndroidManifest.xml");
        SAXReader xmlReader = new SAXReader();
        Document document;
        try {
            document = xmlReader.read(xmlFile);
        } catch (DocumentException e) {
            e.printStackTrace();
            return false;
        }
        Element rootElement = document.getRootElement();

        List<Element> metaDataElements = rootElement.element("application").elements("meta-data");
        for (Element metaDataElement : metaDataElements){
            if(metaDataElement.attribute("name").getValue().equals("HackAppFileName")){
               metaDataElement.addAttribute("value", Config.assetsSourceApkFileName);
                LogUtils.info(TAG, "设置模板Appassets目录存储的被注入Apk名字. " + Config.assetsSourceApkFileName);
            }

            if(metaDataElement.attribute("name").getValue().equals("HackAppPackageName")){
               metaDataElement.addAttribute("value", Config.apkMetaInfo.get("AppPackageName"));
                LogUtils.info(TAG, "设置包装器的启动包名. " + Config.apkMetaInfo.get("AppPackageName"));
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

        LogUtils.info(TAG, "将payload apk中的 AndroidManifest-new.xml 追加到apkbox的 AndroidManifest.xml中.");
        File newXmlFile = new File(Config.payloadApkNewManifestFile);
        SAXReader newXmlReader = new SAXReader();
        Document newDocument;
        try {
            newDocument = newXmlReader.read(newXmlFile);
        } catch (DocumentException e) {
            e.printStackTrace();
            return false;
        }
        Element newRootElement = newDocument.getRootElement();

        for (Element tempElement : newRootElement.elements()){
            rootElement.element("application").add(tempElement.createCopy());
        }
        LogUtils.debug(TAG, rootElement.element("application").toString());

        LogUtils.debug(TAG, rootElement.element("application").elements("meta-data").toString());
        try (FileWriter fileWriter = new FileWriter(xmlFile)) {
            XMLWriter writer = new XMLWriter(fileWriter);
            writer.write( document );
            writer.close();
            LogUtils.info(TAG, "写入模板App的AndroidManifest.xml文件成功.");
        } catch (IOException e) {
            LogUtils.error(TAG, "写入模板App的AndroidManifest.xml文件失败.");
            e.printStackTrace();
            return false;
        }

        LogUtils.info(TAG, "解析并修改模板App的string.xml文件. ");
        xmlFile = new File(Config.apkBoxApkDecodeDir + "/res/values/strings.xml");
        xmlReader = new SAXReader();
        try {
            document = xmlReader.read(xmlFile);
        } catch (DocumentException e) {
            e.printStackTrace();
            return false;
        }
        rootElement = document.getRootElement();
        List<Element> stringElements = rootElement.elements("string");
        for (Element stringElement : stringElements){
            if(stringElement.attribute("name").getValue().equals("app_name")){
                stringElement.setText(Config.apkMetaInfo.get("AppName"));
                LogUtils.info(TAG, "设置模板App显示的名字. " + Config.apkMetaInfo.get("AppName"));
            }
        }
        LogUtils.debug(TAG, rootElement.elements("string").toString());
        try (FileWriter fileWriter = new FileWriter(xmlFile)) {
            XMLWriter writer = new XMLWriter(fileWriter);
            writer.write( document );
            writer.close();
             LogUtils.info(TAG, "写入模板App的string.xml文件成功.");
        } catch (IOException e) {
            LogUtils.error(TAG, "写入模板App的string.xml文件失败.");
            e.printStackTrace();
            return false;
        }

        LogUtils.info(TAG, "修复模板App的values-v31/colors.xml文件. ");
        xmlFile = new File(Config.apkBoxApkDecodeDir + "/res/values-v31/colors.xml");

        Path colorXmlPath = Paths.get(Config.apkBoxApkDecodeDir + "/res/values-v31/colors.xml");

        try {
            replaceFileContent(colorXmlPath, "@android", "@*android");
        }catch (IOException e) {
            LogUtils.info(TAG, "修复模板App的values-v31/colors.xml文件失败. ");
            return false;
        }
        LogUtils.info(TAG, "修复模板App的values-v31/colors.xml文件成功. ");

        return true;
    }

    public void changeApktoolYaml() throws IOException {
        LogUtils.info(TAG, "解析并修改模板App的apktool.yml文件. ");

        Path yamlPath = Paths.get(Config.apkBoxApkDecodeDir + "/apktool.yml");

        if (Config.apkBoxUseOldSdk){
            replaceFileContent(yamlPath, "minSdkVersion: '\\d+'", String.format("minSdkVersion: '%s'", 16));
            replaceFileContent(yamlPath, "targetSdkVersion: '\\d+'", String.format("targetSdkVersion: '%s'", 22));
        }else {
            replaceFileContent(yamlPath, "minSdkVersion: '\\d+'", String.format("minSdkVersion: '%s'", Config.apkMetaInfo.get("AppMinSdkVersion")));
            replaceFileContent(yamlPath, "targetSdkVersion: '\\d+'", String.format("targetSdkVersion: '%s'", Config.apkMetaInfo.get("AppTargetSdkVersion")));
        }
        replaceFileContent(yamlPath, "versionName: '.*?'", String.format("versionName: '%s'", Config.apkMetaInfo.get("AppVersionName")));
        replaceFileContent(yamlPath, "versionCode: '.*?'", String.format("versionCode: '%s'", Config.apkMetaInfo.get("AppVersionCode")));

        LogUtils.info(TAG, "修改模板App的minSdkVersion、targetSdkVersion、versionName、versionCode信息. ");

        LogUtils.info(TAG, "修改模板App的apktool.yml文件成功. ");

    }
    public void changePackageName() throws IOException {

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
        LogUtils.debug(TAG, String.valueOf(newFileDir.exists()));
        if (!newFileDir.exists()){
            newFileDir.mkdirs();
            LogUtils.info(TAG, "创建新的smali目录: " + newSmaliPath);
        }
        FileUtils.copyDirectory(oldFileDir, newFileDir);
        FileUtils.deleteDirectory(oldFileDir);
        LogUtils.info(TAG, "复制原始smali文件到新的smali目录中.");

        List<File> fileList = (List<File>) FileUtils.listFiles(newFileDir,null,false);
        for (File file : fileList){
            replaceFileContent(Paths.get(file.toString()), oldSmaliPackageName, newSmaliPackageName);
            replaceFileContent(Paths.get(file.toString()), Config.oldPackageName, Config.newPackageName);
            LogUtils.info(TAG, String.format("已修改smali文件: %s, 替换 %s 为 %s , 替换 %s 为 %s .", file.toString(), oldSmaliPackageName, newSmaliPackageName, Config.oldPackageName, Config.newPackageName));
        }
        replaceFileContent(Paths.get(Config.apkBoxApkDecodeDir + "/AndroidManifest.xml"), Config.oldPackageName, Config.newPackageName);
        LogUtils.info(TAG, String.format("已修改AndroidManifest.xml文件. 替换 %s 为 %s ", Config.oldPackageName, Config.newPackageName));
    }

    public void copyAssesResFile () throws IOException {
        LogUtils.info(TAG, "开始复制资源文件到模板App中.");

        FileUtils.copyFile(new File(Config.payloadApkDexZipFilePath), new File(Config.apkBoxApkDecodeDir + String.format("/assets/%s", Config.assetsClassesDexFileName)));
        LogUtils.info(TAG, "已将Payload Apk 的Dex压缩包复制到模板App的assets目录中.");

        FileUtils.copyFile(new File(Config.hackApkFilePath), new File(Config.apkBoxApkDecodeDir + String.format("/assets/%s", Config.assetsSourceApkFileName)));
        LogUtils.info(TAG, "已将被注入Apk复制到模板App的assets目录中.");

        FileUtils.delete(new File(Config.apkBoxApkDecodeDir + "/res/mipmap-xxxhdpi/ic_launcher.png"));
        LogUtils.info(TAG, "已清空模板App中的 mipmap-xxxhdpi 文件夹.");
        FileUtils.copyFile(new File(Config.apkIconFilePath), new File(Config.apkBoxApkDecodeDir + "/res/mipmap-xxxhdpi/ic_launcher." + FilenameUtils.getExtension(Config.apkIconFilePath)));
        if (!Objects.equals(Config.apkAdaptiveIconFilePath, "")){
            FileUtils.copyFile(new File(Config.apkAdaptiveIconFilePath), new File(Config.apkBoxApkDecodeDir + "/res/mipmap-xxxhdpi/ic_launcher_foreground." + FilenameUtils.getExtension(Config.apkIconFilePath)));
        }
        LogUtils.info(TAG, "重新向模板App中的 mipmap-xxxhdpi 文件夹复制图标文件.");

        if (new File(Config.payloadApkDecodeDir + "/res/xml").exists()){
            FileUtils.copyDirectory(new File(Config.payloadApkDecodeDir + "/res/xml"), new File(Config.apkBoxApkDecodeDir + "/res/xml/"));
            LogUtils.info(TAG, "已复制Payload apk中的res/xml目录到520ApkBox中.");
        }

        LogUtils.info(TAG, "所有资源文件已复制完成.");
    }

    public Boolean buildApkFile(){
        LogUtils.info(TAG, "正在进行重新编译模板App.");
        Config.buildApkFilePath = System.getProperty("user.dir") + "/520ApkHook.apk";

        return runJar(new File(Config.apkToolFilePath), new String[]{"b", "-o", Config.buildApkFilePath, Config.apkBoxApkDecodeDir});
    }

    public Boolean signerApk(){
        LogUtils.info(TAG, "正在对模板App重新进行签名.");

        return runJar(new File(Config.apkSignerFilePath), new String[]{"sign", "--ks", Config.apkKeyStoreFilePath, "-ks-pass", "pass:p@ssw0rd", Config.buildApkFilePath});
    }

    public Boolean runJar(File file, String[] args) {
        if (Config.IsJar){
            final String mainClass;
            final JarFile jarFile;
            try {
                jarFile = new JarFile(file);
                try {
                    final Manifest manifest = jarFile.getManifest();
                    mainClass = manifest.getMainAttributes().getValue("Main-Class");
                } finally {
                    jarFile.close();
                }
                final URLClassLoader child = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader());
                final Class<?> classToLoad = Class.forName(mainClass, true, child);
                final Method method = classToLoad.getDeclaredMethod("main", String[].class);
                final Object[] arguments = {args};
                method.invoke(null, arguments);
            } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }else {
            String[] command = (String[]) ArrayUtils.addAll(new String[]{"java", "-jar", file.getAbsolutePath()}, args);
            try{
                Process pid = Runtime.getRuntime().exec(command);
                // 获取外部程序标准输出流
                new Thread(new OutputHandlerRunnable(pid.getInputStream())).start();
                // 获取外部程序标准错误流
                new Thread(new OutputHandlerRunnable(pid.getErrorStream())).start();
                pid.waitFor();
            }catch (IOException | InterruptedException e){
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void replaceFileContent(Path filePath, String regex, String repl) throws IOException {
//      Path filePath =   Paths.get(String "filePath")
        String readFileData = Files.readString(filePath);

        readFileData = readFileData.replaceAll(regex, repl);

        FileWriter fileWriter = new FileWriter(filePath.toFile());
        BufferedWriter byteWrite = new BufferedWriter(fileWriter);
        byteWrite.write(readFileData);
        byteWrite.close();
    }

    private static class OutputHandlerRunnable implements Runnable {
        private final InputStream in;
        public OutputHandlerRunnable(InputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            try (BufferedReader bufr = new BufferedReader(new InputStreamReader(this.in))) {
                String line = null;
                while ((line = bufr.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
