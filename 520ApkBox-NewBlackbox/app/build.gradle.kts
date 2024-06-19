plugins {
    alias(libs.plugins.android.application)
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("../Android.keystore")
            storePassword = "p@ssw0rd"
            keyAlias = "Android"
            keyPassword = "p@ssw0rd"
        }
    }
    namespace = (rootProject.ext["packagename"] as String)
    compileSdk = (rootProject.ext["compileSdk"] as Int)

    defaultConfig {
        applicationId = (rootProject.ext["appid"] as String)
        minSdk = (rootProject.ext["minSdk"] as Int)
        targetSdk = (rootProject.ext["targetSdk"] as Int)
        versionCode = (rootProject.ext["versioncode"] as Int)
        versionName = (rootProject.ext["appversion"] as String)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a")
            isUniversalApk = false
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(project(":Bcore"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.zip4j)
}