plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {

    namespace = "com.vcore"
    compileSdk = (rootProject.ext["compileSdk"] as Int)


    defaultConfig {
        minSdk = (rootProject.ext["minSdk"] as Int)
        consumerProguardFiles("consumer-rules.pro")
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk.apply{
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
        }
     //   signingConfig = signingConfigs.getByName("debug")

    }
    val cmake = rootProject.ext["cmakeVersion"] as String
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = cmake
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false  //not tested yet
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures{
        aidl = true
        prefab = true
        viewBinding = true
    }

    packagingOptions.apply {
        jniLibs {
            // 排除所有其他文件模式
            excludes.add("**/libshadowhook.so")
        }
    }

}

// it make update dependency update easy
val ktxversion = "1.13.1"
val stdlib_version = "1.9.24"
val hiddenapibypass = "4.3"
val xcrashversion = "3.1.0"
val shadowhook = "1.0.9"
val googlematerial = "1.12.0"


dependencies {
    implementation("androidx.appcompat:appcompat") {
        exclude(group = "com.android.support", module = "support-compat")
    }
    implementation("androidx.core:core-ktx:$ktxversion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$stdlib_version")
    implementation ("com.iqiyi.xcrash:xcrash-android-lib:$xcrashversion")
    implementation("com.google.android.material:material:$googlematerial")
    implementation ("org.lsposed.hiddenapibypass:hiddenapibypass:$hiddenapibypass")
    implementation("com.bytedance.android:shadowhook:$shadowhook")
    implementation ("top.canyie.pine:core:0.2.9")
    implementation ("top.canyie.pine:xposed:0.1.0")

}