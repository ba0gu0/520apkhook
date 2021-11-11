#!/usr/bin/python3
# -*- coding: utf-8 -*-

OPTIONS = {
    # test dir
    'TestDir': 'WorkDir',
    'TestDirApkFile': f'WorkDir/apkfile',
    'TestDirDexFile': f'WorkDir/dexfile',
    'TestDirUnZipApkPath': f'WorkDir/apkfile/app',
    'TestDirUnZipXmlPath': f'WorkDir/apkfile/xml',
    'TestDirAppUnSmaliPath': f'WorkDir/dexfile/app',
    'TestDirMsfUnSmaliPath': f'WorkDir/dexfile/msf',

    # zip apk file
    'ZipExtractPath': 'WorkDir/apkfile/app/',
    'ZipCompressPath': 'WorkDir/apkfile/app/',
    'ZipFinishApk': 'WorkDir/AllFinish.apk',

    # msf dex file
    'MsfBaseDexFile': 'libs/MsfDexDir/classes.dex',
    'MsfBuildDexFile': 'WorkDir/dexfile/msf/classes.dex',
    'MsfHandler': 'WorkDir/handler.rc',

    # Encrypte dex file
    'JavaPath': r'',
    'JarFilePath': 'libs/jar/',

    # 如果需要自定义的签名证书，请在此处修改文件路径和证书密码，路径使用相对或者绝对路径
    'Keystorefile': 'libs/520.keystore',
    'KeystorePass': '520ApkHook',


    'AESCryptKey': "huangdh'l,.AMWK;",

    # Steady Dex File
    'SteadyDexFile': 'libs/ShellDexDir/classes.dex',
    'SteadyLibFile': 'libs/ShellDexDir/jni_lib',

    # AndroidManifest.xml
    'SteadyLauncherActivity': 'com.sakuqi.steady.SteadyApplication',
    'UsesPermission': ['android.permission.READ_CONTACTS', 'android.permission.RECORD_AUDIO', 'android.permission.READ_PHONE_STATE', 'android.permission.WAKE_LOCK', 'android.permission.READ_CALL_LOG', 'android.permission.SEND_SMS', 'android.permission.CAMERA', 'android.permission.RECEIVE_BOOT_COMPLETED', 'android.permission.INTERNET', 'android.permission.ACCESS_WIFI_STATE', 'android.permission.WRITE_CALL_LOG', 'android.permission.READ_SMS', 'android.permission.CHANGE_WIFI_STATE', 'android.permission.ACCESS_NETWORK_STATE', 'android.permission.ACCESS_FINE_LOCATION', 'android.permission.SET_WALLPAPER', 'android.permission.RECEIVE_SMS', 'android.permission.WRITE_EXTERNAL_STORAGE', 'android.permission.WRITE_CONTACTS', 'android.permission.ACCESS_COARSE_LOCATION', 'android.permission.WRITE_SETTINGS', 'android.permission.CALL_PHONE', 'android.permission.ACCESS_SUPERUSER'],
}


