package org.a520apkhook;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Random;


public class GetPayloadApkInfo {
    private final String TAG = "GetPayloadApkInfo";
    HackApk hackApk = new HackApk();

    public GetPayloadApkInfo() {
        // 初始化, 打开apk文件
    }

    public Boolean decodeApkFile (){
        LogUtils.info(TAG, "使用apktool反编译 Payload Apk, " + Config.payloadApkFilePath);
        return hackApk.runJar(new File(Config.apkToolFilePath), new String[]{"d", Config.payloadApkFilePath, "-s", "-f", "-o", Config.payloadApkDecodeDir});
    }

    public Boolean getApkMetaInfo(){
        LogUtils.info(TAG, "解析 Payload Apk 的AndroidManifest.xml文件, 并创建新的 AndroidManifest-New.xml文件. ");

        File xmlFile = new File(Config.payloadApkDecodeDir + "/AndroidManifest.xml");
        SAXReader xmlReader = new SAXReader();
        Document document;
        try {
            document = xmlReader.read(xmlFile);
        } catch (DocumentException e) {
            e.printStackTrace();
            return false;
        }
        Element rootElement = document.getRootElement();
        LogUtils.debug(TAG, rootElement.element("application").toString());
        if (rootElement.attribute("package") != null){
            Config.payloadApkPackageName = rootElement.attribute("package").getStringValue();
        }
        Document newDocument = DocumentHelper.createDocument();
        Element applicationNode = newDocument.addElement("application");
        applicationNode.addAttribute("xmlns:android", "http://schemas.android.com/apk/res/android");

        if (rootElement.element("application") != null && rootElement.element("application").attribute("name") != null){
            Config.payloadApkApplicationName = rootElement.element("application").attribute("name").getStringValue();
            if (Config.payloadApkApplicationName.startsWith(".")) {
                Config.payloadApkApplicationName = Config.payloadApkPackageName + Config.payloadApkApplicationName;
            }
            LogUtils.info(TAG, "成功获取到Payload Apk的ApplicationName. ");
        }

        List<Element> metaDataElements = rootElement.element("application").elements("meta-data");
        for (Element metaDataElement : metaDataElements){
            applicationNode.add(metaDataElement.createCopy());
        }
        LogUtils.debug(TAG, metaDataElements.toString());

        List<Element> serviceElements = rootElement.element("application").elements("service");
        for (Element serviceElement : serviceElements){
            if (serviceElement.attribute("name").getStringValue().startsWith(".")){
                serviceElement.addAttribute("name", Config.payloadApkPackageName + serviceElement.attribute("name").getStringValue());
            }
            applicationNode.add(serviceElement.createCopy());
        }
        LogUtils.debug(TAG, serviceElements.toString());

        List<Element> receiverElements = rootElement.element("application").elements("receiver");
        for (Element receiverElement : receiverElements){
            if (receiverElement.attribute("name").getStringValue().startsWith(".")){
                receiverElement.addAttribute("name", Config.payloadApkPackageName + receiverElement.attribute("name").getStringValue());
            }
            applicationNode.add(receiverElement.createCopy());
        }
        LogUtils.debug(TAG, receiverElements.toString());

        List<Element> providerElements = rootElement.element("application").elements("provider");
        for (Element providerElement : providerElements){
            if (providerElement.attribute("name").getStringValue().startsWith(".")){
                providerElement.addAttribute("name", Config.payloadApkPackageName + providerElement.attribute("name").getStringValue());
            }
            applicationNode.add(providerElement.createCopy());
        }
        LogUtils.debug(TAG, providerElements.toString());

        // 获取所有名为<activity>的子节点
        List<Element> activityElements = rootElement.element("application").elements("activity");
        // 将符合条件的<activity>元素添加到<application>节点下
        for (Element activityElement : activityElements) {

            if (activityElement.attribute("name").getStringValue().startsWith(".")){
                activityElement.addAttribute("name", Config.payloadApkPackageName + activityElement.attribute("name").getStringValue());
            }

            Node actionNode = activityElement.selectSingleNode("intent-filter/action[@android:name='android.intent.action.MAIN']");
            if (actionNode == null) {
                applicationNode.add(activityElement.createCopy());
            }else {

                Config.payloadApkMainActivityName = activityElement.attribute("name").getStringValue();

                Element tempActivityNode =  applicationNode.addElement("activity");
                tempActivityNode.addAttribute("android:name", Config.payloadApkMainActivityName);
                tempActivityNode.addAttribute("android:excludeFromRecents", "true");

                LogUtils.info(TAG, "成功获取到 Payload Apk 的MainActivityName. ");
            }
        }
        LogUtils.debug(TAG, activityElements.toString());

        Config.payloadApkNewManifestFile = Config.payloadApkDecodeDir + "/AndroidManifest-New.xml";

        try (FileWriter fileWriter = new FileWriter(new File(Config.payloadApkNewManifestFile))) {
            XMLWriter writer = new XMLWriter(fileWriter);
            writer.write( newDocument );
            writer.close();
             LogUtils.info(TAG, "获取 payload Apk 文件中的Provider、Receiver、Service、Meta-data、Activity成功.");
        } catch (IOException e) {
            LogUtils.error(TAG, "写入模板App的string.xml文件失败.");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public String generatePassword(int length) {
        String passwordSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        char[] password = new char[length];
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(passwordSet.length());
            password[i] = passwordSet.charAt(index);
        }
        return new String(password);
    }
    public Boolean zipAndEncryptFiles() {
        LogUtils.info(TAG, "尝试对 Payload Apk的dex文件进行加密压缩. " + Config.payloadApkFilePath);

        Config.payloadApkDexZipFilePath = Config.payloadApkDecodeDir + "/classes.zip";
        Config.payloadApkDexZipFilePass = generatePassword(8);

        LogUtils.info(TAG,   "dex文件加密密码为: " + Config.payloadApkDexZipFilePass);
        File inputFolder = new File(Config.payloadApkDecodeDir);
        File[] dexFiles = inputFolder.listFiles(file -> file.isFile() && file.getName().endsWith(".dex"));

        if (dexFiles == null || dexFiles.length == 0) {
            return false;
        }

        List<File> dexFileList = new ArrayList<>(Arrays.asList(dexFiles));

        ZipFile zipFile = new ZipFile(Config.payloadApkDexZipFilePath, Config.payloadApkDexZipFilePass.toCharArray());

        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);

        try {
            zipFile.addFiles(dexFileList, zipParameters);
        }
        catch (ZipException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }



}
