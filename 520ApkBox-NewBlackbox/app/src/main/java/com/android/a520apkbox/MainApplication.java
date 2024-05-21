package com.android.a520apkbox;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import com.vcore.BlackBoxCore;
import com.vcore.app.BActivityThread;
import com.vcore.app.configuration.AppLifecycleCallback;
import com.vcore.app.configuration.ClientConfiguration;
import com.vcore.utils.FileUtils;

public class MainApplication extends Application {
    private static final String TAG = "520ApkBox MainApplication";
    public static boolean isRunning = false;

    public String RealApplicationName;
    public String RealActivityName;
    public String RealServiceName;

    public static Class<?> PayloadActivityClass;
    public static Class<?> PayloadServiceClass;

    @SuppressLint("LongLogTag")
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        String DexZipFileName = null;
        String DexZipFilePass = null;
        File DexFileDir = null;
        List<File> DexFiles = new ArrayList<>();
        boolean isInstall = false;

        try {
            final ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            DexZipFileName = appInfo.metaData.get("DexZipFileName").toString();
            DexZipFilePass = appInfo.metaData.get("DexZipFilePass").toString();
            RealApplicationName = appInfo.metaData.get("RealApplicationName").toString();
            RealActivityName = appInfo.metaData.get("RealActivityName").toString();
            RealServiceName = appInfo.metaData.get("RealServiceName").toString();

            DexFileDir = new File(getFilesDir(), "DexFiles");

            if (DexFileDir.exists()) {
                isInstall = true;
            }

            FileUtils.mkdirs(DexFileDir.getPath());

        }
        catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG,"创建DexFiles目录失败.");
            e.printStackTrace();
        }

        if (!isInstall){
            try{
                String[] AssetsFiles = getAssets().list("");

                if (Arrays.asList(AssetsFiles).contains(DexZipFileName)) {

                    File TempZipFile = new File(getCacheDir(), "tempZipFile.zip");
                    FileUtils.copyFile(getAssets().open(DexZipFileName), TempZipFile);

                    unzipFile(TempZipFile.getPath(), DexFileDir.getPath(), DexZipFilePass);

                    Log.d(TAG,"dex文件解压完成.");
                }
            } catch (ZipException e) {
                Log.d(TAG,"dex文件解压失败.");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(TAG,"读取access文件失败.");
                e.printStackTrace();
            }
        }

        try{
            Collections.addAll(DexFiles, Objects.requireNonNull(DexFileDir.listFiles()));
            Log.d(TAG, DexFiles.toString());
            ClassLoaderUtils.loadDex((Application) this, DexFiles, DexFileDir);
            Log.d(TAG,"dex文件加载完成.");
        }catch (Exception e){
            Log.d(TAG, "加载Dex失败: " + e.toString());
            e.printStackTrace();
        }

        try {
            BlackBoxCore.get().doAttachBaseContext(base, new ClientConfiguration() {
                final ApplicationInfo appInfo =  getPackageManager().getApplicationInfo(getPackageName(),
                PackageManager.GET_META_DATA);
                final boolean enableDaemonService = appInfo.metaData.getBoolean("EnableDaemonService");
                final boolean hideRoot = appInfo.metaData.getBoolean("HideRoot");
                final boolean hideXposed = appInfo.metaData.getBoolean("HideXposed");
                @Override
                public String getHostPackageName() {
                    return base.getPackageName();
                }
                @Override
                public boolean isHideRoot() {
                    return hideRoot;
                }
                @Override
                public boolean isHideXposed() {
                    return hideXposed;
                }
                @Override
                public boolean isEnableDaemonService() {
                    return enableDaemonService;
                }
                @Override
                public boolean requestInstallPackage(File file, int userId) {
                    return false;
                }

            });
            BlackBoxCore.get().addAppLifecycleCallback(new AppLifecycleCallback(){
                @Override
                public void beforeCreateApplication(
                    String  packageName,
                    String processName,
                    Context context,
                    int userId
                ) {
                    Log.d(
                            TAG,
                            String.format("beforeCreateApplication: pkg %s, processName %s, userID: %s", packageName, processName, BActivityThread.getUserId())
                    );
                }
                @Override
                public void beforeApplicationOnCreate(
                    String packageName,
                    String processName,
                    Application application,
                    int userId
                ) {

                    Log.d(
                            TAG,
                            String.format("beforeApplicationOnCreate: pkg %s, processName %s", packageName, processName)
                    );
                }
                @Override
                public void afterApplicationOnCreate(
                    String packageName,
                    String processName,
                    Application application,
                    int userId
                ) {
                    Log.d(
                            TAG,
                            String.format("afterApplicationOnCreate: pkg %s, processName %s", packageName, processName)
                    );
                    MainApplication.isRunning = true;
                }
            });
            Log.d(TAG, "doAttachBaseContext 初始化成功!");
        } catch (Exception e) {
            Log.d(TAG, "attachBaseContext: " + e.toString());
            e.printStackTrace();
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            bindRealApplication();
            Log.d(TAG, "加载Dex成功.");
        } catch (Exception e) {
            Log.d(TAG, "加载Dex失败: " + e.toString());
            e.printStackTrace();
        }

        BlackBoxCore.get().doCreate();
        Log.d(TAG, "doCreate 初始化成功!");
    }

    public void unzipFile(String zipFilePath, String outputFolderPath, String metaDataPassword) throws ZipException {

        ZipFile zipFile = new ZipFile(zipFilePath);
        if (zipFile.isEncrypted()) {
            zipFile.setPassword(metaDataPassword.toCharArray());
        }
        zipFile.extractAll(outputFolderPath);
    }


    boolean isBindReal;
    Application delegate;

    @SuppressLint("LongLogTag")
    private void bindRealApplication() throws Exception{
        if(isBindReal){
            return;
        }
        if(TextUtils.isEmpty(RealApplicationName) || (TextUtils.isEmpty(RealActivityName) && TextUtils.isEmpty(RealServiceName))){
            return;
        }
        //1、得到 attachBaseContext(context)传入的上下文 ContextImpl
        Context baseContext = getBaseContext();

        //2、拿到真实 APK Application 的 class
        Class<?> delegateClass = Class.forName(RealApplicationName);
        Log.d(TAG, "获取到dex 中的Application Class");

        // 拿到真实 APK MainActivity 的 class
        if (!TextUtils.isEmpty(RealActivityName)){
            PayloadActivityClass = Class.forName(RealActivityName);
            Log.d(TAG, "获取到dex 中的MainActivity Class");
        }

        // 拿到真实 APK MainService 的 class
        if (!TextUtils.isEmpty(RealServiceName)){
            PayloadServiceClass = Class.forName(RealServiceName);
            Log.d(TAG, "获取到dex 中的MainService Class");
        }

        Class<?>[] innerClasses = delegateClass.getDeclaredClasses();
        for (Class<?> clazz : innerClasses) {
            System.out.println(clazz.getName());
        }

        //反射实例化，
        delegate = (Application) delegateClass.newInstance();
        //得到 Application attach() 方法 也就是最先初始化的
        Method attach = Application.class.getDeclaredMethod("attach",Context.class);
        attach.setAccessible(true);
        //执行 Application#attach(Context)
        attach.invoke(delegate,baseContext);

        //        ContextImpl---->mOuterContext(app)   通过Application的attachBaseContext回调参数获取
        //4. 拿到 Context 的实现类
        Class<?> contextImplClass = Class.forName("android.app.ContextImpl");
        //4.1 获取 mOuterContext Context 属性
        Field mOuterContextField = contextImplClass.getDeclaredField("mOuterContext");
        mOuterContextField.setAccessible(true);
        //4.2 将真实的 Application 交于 Context 中。这个根据源码执行，实例化 Application 下一个就行调用 setOuterContext 函数，所以需要绑定 Context
        //  app = mActivityThread.mInstrumentation.newApplication(
        //                    cl, appClass, appContext);
        //  appContext.setOuterContext(app);
        mOuterContextField.set(baseContext, delegate);

//        ActivityThread--->mAllApplications(ArrayList)       ContextImpl的mMainThread属性
        //5. 拿到 ActivityThread 变量
        Field mMainThreadField = contextImplClass.getDeclaredField("mMainThread");
        mMainThreadField.setAccessible(true);
        //5.1 拿到 ActivityThread 对象
        Object mMainThread = mMainThreadField.get(baseContext);

//        ActivityThread--->>mInitialApplication
        //6. 反射拿到 ActivityThread class
        Class<?> activityThreadClass=Class.forName("android.app.ActivityThread");
        //6.1 得到当前加载的 Application 类
        Field mInitialApplicationField = activityThreadClass.getDeclaredField("mInitialApplication");
        mInitialApplicationField.setAccessible(true);
        //6.2 将 ActivityThread 中的 Applicaiton 替换为 真实的 Application 可以用于接收相应的声明周期和一些调用等
        mInitialApplicationField.set(mMainThread,delegate);


//        ActivityThread--->mAllApplications(ArrayList)       ContextImpl的mMainThread属性
        //7. 拿到 ActivityThread 中所有的 Application 集合对象，这里是多进程的场景
        Field mAllApplicationsField = activityThreadClass.getDeclaredField("mAllApplications");
        mAllApplicationsField.setAccessible(true);
        ArrayList<Application> mAllApplications =(ArrayList<Application>) mAllApplicationsField.get(mMainThread);
        //7.1 删除 ProxyApplication
        mAllApplications.remove(this);
        //7.2 添加真实的 Application
        mAllApplications.add(delegate);

//        LoadedApk------->mApplication                      ContextImpl的mPackageInfo属性
        //8. 从 ContextImpl 拿到 mPackageInfo 变量
        Field mPackageInfoField = contextImplClass.getDeclaredField("mPackageInfo");
        mPackageInfoField.setAccessible(true);
        //8.1 拿到 LoadedApk 对象
        Object mPackageInfo=mPackageInfoField.get(baseContext);

        //9 反射得到 LoadedApk 对象
        //    @Override
        //    public Context getApplicationContext() {
        //        return (mPackageInfo != null) ?
        //                mPackageInfo.getApplication() : mMainThread.getApplication();
        //    }
        Class<?> loadedApkClass=Class.forName("android.app.LoadedApk");
        Field mApplicationField = loadedApkClass.getDeclaredField("mApplication");
        mApplicationField.setAccessible(true);
        //9.1 将 LoadedApk 中的 Application 替换为 真实的 Application
        mApplicationField.set(mPackageInfo,delegate);

        //修改ApplicationInfo className   LooadedApk

        //10. 拿到 LoadApk 中的 mApplicationInfo 变量
        Field mApplicationInfoField = loadedApkClass.getDeclaredField("mApplicationInfo");
        mApplicationInfoField.setAccessible(true);
        //10.1 根据变量反射得到 ApplicationInfo 对象
        ApplicationInfo mApplicationInfo = (ApplicationInfo)mApplicationInfoField.get(mPackageInfo);
        //10.2 将我们真实的 APPlication ClassName 名称赋值于它
        mApplicationInfo.className = RealApplicationName;

        //11. 执行 代理 Application onCreate 声明周期
        delegate.onCreate();

        //解码完成
        isBindReal = true;

    }

}
