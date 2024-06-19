package org.a520apkhook;

import com.ibm.icu.util.ULocale;
import java.util.ArrayList;
import java.util.List;

public class TestCode1 {
    private List<AppName> appNames;

    // Getters and setters for appNames
    public List<AppName> getAppNames() {
        return appNames;
    }

    public void setAppNames(List<AppName> appNames) {
        this.appNames = appNames;
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

    private static List<AppName> extractAppName(String output) {
        List<AppName> appNames = new ArrayList<>();
        String baseLabel = "application-label";
        int index = 0;

        while ((index = output.indexOf(baseLabel, index)) != -1) {
            int startIndex = output.indexOf("'", index) + 1;
            int endIndex = output.indexOf("'", startIndex);
            if (startIndex > 0 && endIndex > startIndex) {

                String language;
                String name = output.substring(startIndex, endIndex);

                if (index + baseLabel.length() == output.indexOf(":", index)){
                    language = "default";
                }else {
                    language = output.substring(index + baseLabel.length() + 1, output.indexOf(":", index)).trim();
                    if (language.contains("-")){
                        ULocale locale = ULocale.forLanguageTag(language);
                        String script = locale.getScript();
                        String region = locale.getCountry();

                        if (!script.isEmpty() && !region.isEmpty()) {
                            language = "b+" + locale.getLanguage() + "+" + script + "+" + region;
                        } else if (!script.isEmpty()) {
                            language = "b+" + locale.getLanguage() + "+" + script;
                        } else if (!region.isEmpty()) {
                            language = locale.getLanguage() + "-r" + region;
                        } else {
                            language = locale.getLanguage();
                        }
                    }
                }
                appNames.add(new AppName(language, name));
            }
            index = endIndex + 1;
        }

        return appNames;
    }

    public static void main(String[] args) {
        // Example usage
        String output = "package: name='bin.mt.plus' versionCode='24051865' versionName='2.15.5' platformBuildVersionName='14' platformBuildVersionCode='34' compileSdkVersion='34' compileSdkVersionCodename='14'\n" +
                "sdkVersion:'21'\n" +
                "targetSdkVersion:'30'\n" +
                "application-label:'WeChat'\n" +
                "application-label-as:'WeChat'\n" +
                "application-label-az:'WeChat'\n" +
                "application-label-be:'WeChat'\n" +
                "application-label-bn:'WeChat'\n" +
                "application-label-bs:'WeChat'\n" +
                "application-label-en:'WeChat'\n" +
                "application-label-en-AU:'WeChat'\n" +
                "application-label-en-CA:'WeChat'\n" +
                "application-label-en-GB:'WeChat'\n" +
                "application-label-en-IN:'WeChat'\n" +
                "application-label-en-XC:'WeChat'\n" +
                "application-label-es-US:'WeChat'\n" +
                "application-label-et:'WeChat'\n" +
                "application-label-eu:'WeChat'\n" +
                "application-label-fa:'WeChat'\n" +
                "application-label-fr-CA:'WeChat'\n" +
                "application-label-gl:'WeChat'\n" +
                "application-label-gu:'WeChat'\n" +
                "application-label-hi:'WeChat'\n" +
                "application-label-hy:'WeChat'\n" +
                "application-label-is:'WeChat'\n" +
                "application-label-iw:'WeChat'\n" +
                "application-label-ka:'WeChat'\n" +
                "application-label-kk:'WeChat'\n" +
                "application-label-km:'WeChat'\n" +
                "application-label-kn:'WeChat'\n" +
                "application-label-ky:'WeChat'\n" +
                "application-label-mk:'WeChat'\n" +
                "application-label-ml:'WeChat'\n" +
                "application-label-mn:'WeChat'\n" +
                "application-label-mr:'WeChat'\n" +
                "application-label-ne:'WeChat'\n" +
                "application-label-or:'WeChat'\n" +
                "application-label-pa:'WeChat'\n" +
                "application-label-pl:'WeChat'\n" +
                "application-label-pt-BR:'WeChat'\n" +
                "application-label-pt-PT:'WeChat'\n" +
                "application-label-si:'WeChat'\n" +
                "application-label-sq:'WeChat'\n" +
                "application-label-sr:'WeChat'\n" +
                "application-label-sr-Latn:'WeChat'\n" +
                "application-label-sv:'WeChat'\n" +
                "application-label-sw:'WeChat'\n" +
                "application-label-ta:'WeChat'\n" +
                "application-label-te:'WeChat'\n" +
                "application-label-tl:'WeChat'\n" +
                "application-label-uk:'WeChat'\n" +
                "application-label-ur:'WeChat'\n" +
                "application-label-uz:'WeChat'\n" +
                "application-label-zh-CN:'微信'\n" +
                "application-label-zh-TW:'WeChat'\n" +
                "application-label-zu:'WeChat'";
        List<AppName> appNames = extractAppName(output);
        for (AppName appName : appNames) {
            System.out.println(appName);
        }
    }
}