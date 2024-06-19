package org.a520apkhook;

import java.util.List;

public class ApkMetaInfo {
    private String appPackageName;
    private String appVersionName;
    private String appVersionCode;
    private String appMinSdkVersion;
    private String appTargetSdkVersion;
    private List<AppName> appNames;

    // Getters and setters
    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppMinSdkVersion() {
        return appMinSdkVersion;
    }

    public void setAppMinSdkVersion(String appMinSdkVersion) {
        this.appMinSdkVersion = appMinSdkVersion;
    }

    public String getAppTargetSdkVersion() {
        return appTargetSdkVersion;
    }

    public void setAppTargetSdkVersion(String appTargetSdkVersion) {
        this.appTargetSdkVersion = appTargetSdkVersion;
    }

    public List<AppName> getAppNames() {
        return appNames;
    }

    public void setAppNames(List<AppName> appNames) {
        this.appNames = appNames;
    }

    @Override
    public String toString() {
        return "ApkMetaInfo{" +
                "appPackageName='" + appPackageName + '\'' +
                ", appVersionName='" + appVersionName + '\'' +
                ", appVersionCode='" + appVersionCode + '\'' +
                ", appMinSdkVersion='" + appMinSdkVersion + '\'' +
                ", appTargetSdkVersion='" + appTargetSdkVersion + '\'' +
                ", appNames=" + appNames +
                '}';
    }

    public static class AppName {
        private final String language;
        private final String name;

        public AppName(String language, String name) {
            this.language = language;
            this.name = name;
        }

        public String getLanguage() {
            return language;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Language: " + language + ", Name: " + name;
        }
    }
}