package com.android.a520apkbox;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import pub.devrel.easypermissions.EasyPermissions;

public class PermissionActivity extends AppCompatActivity {

    private static final String TAG = "520ApkBox PermissionActivity";
    private static final int REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.d(TAG, "权限足够");
            go();
        }else {
            Log.d(TAG, "权限不足");
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_to_setting),
                REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.d(TAG, "权限已被授予");
                go();
            } else {
                Log.d(TAG, "权限被拒绝");
                // 处理权限被拒绝的情况，比如向用户展示为何需要权限等
                // 这里简单地关闭应用
//                finish();
            }
        }
    }

    @Override
    protected void onNewIntent(@Nullable Intent intent) {
        super.onNewIntent(intent);
        go();
    }

    private void go() {
        // 在 WelcomeActivity 中启动 MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}