package org.a520apkhook;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.io.File;

import static org.a520apkhook.CmdUtils.*;

public class ByPassPayloadApk {

    private final String TAG = "ByPassPayloadApk";

    public ByPassPayloadApk() {
        // 初始化, 打开apk文件
    }

    public Boolean decodeApkFile (){
        LogUtils.info(TAG, "使用apktool反编译 Payload Apk, " + Config.payloadApkFilePath);
        return RunCommand(new File(Config.apkToolFilePath), new String[]{"d", Config.payloadApkFilePath, "-f", "-o", Config.payloadApkDecodeDir});
    }

    public Boolean buildApkFile (){
        LogUtils.info(TAG, "使用apktool 编译 Payload Apk, " + Config.byPassPayloadApkFilePath);
        return RunCommand(new File(Config.apkToolFilePath), new String[]{"b", Config.payloadApkDecodeDir, "-f", "-o", Config.byPassPayloadApkFilePath});
    }

    private static String getRandomString() {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            char c = characters.charAt(random.nextInt(characters.length()));
            if (i == 0 && Character.isDigit(c)) {
                i--;
                continue;
            }
            result.append(c);
        }
        return result.toString();
    }

    public void changePayloadApkPackageName() throws IOException {

        LogUtils.info(TAG, "解析 Payload Apk 的AndroidManifest.xml文件, 并随机修改包名文件. ");

        File xmlFile = new File(Config.payloadApkDecodeDir + "/AndroidManifest.xml");
        Document document = readXmlFile(xmlFile);

        Element rootElement = document.getRootElement();
        LogUtils.debug(TAG, rootElement.element("application").toString());

        String payloadApkOldPackageName = "";

        if (rootElement.attribute("package") != null){
            payloadApkOldPackageName = rootElement.attribute("package").getStringValue();
        }else {
            LogUtils.warn(TAG, "无法获取到 PayloadApk 的包名。");
            throw new IOException("无法获取到 PayloadApk 的包名。");
        }

        String[] packageNameParts = payloadApkOldPackageName.split("\\.");

        StringBuilder newPackageName = new StringBuilder();

        for (String part : packageNameParts) {
            if (newPackageName.length() > 0) {
                newPackageName.append(".");
            }
            newPackageName.append(getRandomString());
        }

        String payloadApkNewPackageName = newPackageName.toString();

        LogUtils.info(TAG, "旧包名: " + payloadApkOldPackageName + ", 新包名: " + payloadApkNewPackageName);

        String oldSmaliPackagePath = payloadApkOldPackageName.replaceAll("\\.", "/");
        String newSmaliPackagePath = payloadApkNewPackageName.replaceAll("\\.", "/");

        File payloadApkDir = new File(Config.payloadApkDecodeDir);
        File[] smaliDirs = payloadApkDir.listFiles((dir, name) -> name.startsWith("smali"));

        String oldSmaliPath = null;
        String newSmaliPath = null;

        for (File smaliDir : smaliDirs) {
            File testPath = new File(smaliDir, oldSmaliPackagePath);

            LogUtils.debug(TAG, testPath.getAbsolutePath());

            if (testPath.exists()) {
                oldSmaliPath = testPath.getPath();
                newSmaliPath = new File(smaliDir, newSmaliPackagePath).getPath();
                break;
            }
        }

        if (oldSmaliPath == null) {
            LogUtils.warn(TAG, "未找到原始smali文件路径: " + oldSmaliPackagePath);
            throw new IOException("未找到原始smali文件路径");
        } else {
            LogUtils.info(TAG, "原始smali文件路径: " + oldSmaliPath);
            LogUtils.info(TAG, "修改后smali文件路径: " + newSmaliPath);
        }

        File oldFileDir = new File(oldSmaliPath);
        File newFileDir = new File(newSmaliPath);

        if (!newFileDir.exists()){
            if(newFileDir.mkdirs()){
                LogUtils.info(TAG, "成功创建新的smali目录: " + newSmaliPath);
            }else {
                LogUtils.warn(TAG, "目录创建失败: " + newSmaliPath);
                throw new IOException();
            }
        }

        FileUtils.copyDirectory(oldFileDir, newFileDir);
        FileUtils.deleteDirectory(oldFileDir);
        LogUtils.info(TAG, "复制原始smali文件到新的smali目录中.");

        List<File> fileList = (List<File>) FileUtils.listFiles(newFileDir,null,false);

        for (File file : fileList){
            Path filePath = Paths.get(file.toString());
            ReplaceFileContent(filePath, oldSmaliPackagePath, newSmaliPackagePath);
            ReplaceFileContent(filePath, payloadApkOldPackageName, payloadApkNewPackageName);
            LogUtils.info(TAG, String.format("已修改smali文件: %s, 替换 %s 为 %s , 替换 %s 为 %s .", file.toString(), oldSmaliPackagePath, newSmaliPackagePath, payloadApkOldPackageName, payloadApkNewPackageName));
        }

        ReplaceFileContent(Paths.get(Config.payloadApkDecodeDir + "/AndroidManifest.xml"), payloadApkOldPackageName, payloadApkNewPackageName);

        LogUtils.info(TAG, String.format("已修改AndroidManifest.xml文件. 替换 %s 为 %s ", payloadApkOldPackageName, payloadApkNewPackageName));
    }

}
