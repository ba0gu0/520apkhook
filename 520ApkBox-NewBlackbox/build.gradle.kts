// Top-level build file where you can add configuration options common to all sub-projects/modules.
ext.apply {
    // this aapplyes on every projects and dependency
    set("compileSdk", 34)
    set("targetSdk", 28)
    set("minSdk", 23)
    set("ndkVersion", "24.0.8215888")
    set("cmakeVersion", "3.22.1") //latest
    set("ktx_version", "1.12.0") // kotlin dependency version androidx.core:core-ktx
    set("stdlib_version", "1.8.22") // kotlin stdlib version org.jetbrains.kotlin:kotlin-stdlib-jdk7
    set("hiddenapibypass", "4.3") // kotlin stdlib version org.lsposed.hiddenapibypass:hiddenapibypass
    set("xcrashversion", "3.0.0") // com.iqiyi.xcrash:xcrash-android-lib
    set("shadowhook", "1.0.8") // com.bytedance.android:shadowhook:
    set("googlematerial", "1.11.0") // com.google.android.material:material:

    // set properties for app only
    set("appid", "com.android.a520apkbox") // appid
    set("packagename", "com.android.a520apkbox") // main packagename
    set("appversion", "3.0.1-alpha")
    set("versioncode", 3)

}

plugins {
    alias(libs.plugins.android.application) apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.android.library") version "8.3.0" apply false
}