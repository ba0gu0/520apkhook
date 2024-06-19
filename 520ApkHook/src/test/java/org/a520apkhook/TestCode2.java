package org.a520apkhook;

import com.ibm.icu.util.ULocale;

public class TestCode2 {

    public static String processLanguageTag(String languageTag) {
        ULocale locale = ULocale.forLanguageTag(languageTag);
        String language = locale.getLanguage();
        String script = locale.getScript();
        String region = locale.getCountry();

        // 调试信息，查看ULocale是否正确解析了语言标签
        System.out.println("Language Tag: " + languageTag);
        System.out.println("Parsed language: " + language);
        System.out.println("Parsed script: " + script);
        System.out.println("Parsed region: " + region);

        // 使用 ULocale.Builder 构建自定义格式的语言标签
        StringBuilder processedTag = new StringBuilder();

        if (!script.isEmpty() && !region.isEmpty()) {
            processedTag.append("b+").append(language).append("+").append(script).append("+").append(region);
        } else if (!script.isEmpty()) {
            processedTag.append("b+").append(language).append("+").append(script);
        } else if (!region.isEmpty()) {
            if ("419".equals(region)) {
                processedTag.append("b+").append(language).append("+419");
            } else {
                processedTag.append(language).append("-r").append(region);
            }
        } else {
            processedTag.append(language);
        }

        return processedTag.toString();
    }

    public static void main(String[] args) {
        String[] testTags = {
            "am", // Amharic
            "az", // Azerbaijani
            "es-419", // Latin America and Caribbean Spanish
            "zh-Hans-CN", // Simplified Chinese for China
            "sr-Latn", // Serbian in Latin script
            "az-Cyrl", // Azerbaijani in Cyrillic script
            "en-GB", // British English
            "es-MX", // Mexican Spanish
            "pt-BR", // Brazilian Portuguese
            "ja", // Japanese
            "de-DE", // German for Germany
            "fr-CA", // Canadian French
            "en-AU", // Australian English
            "es-US", // US Spanish
            "sv-SE" // Swedish for Sweden
        };

        for (String tag : testTags) {
            System.out.println("Original: " + tag + " -> Processed: " + processLanguageTag(tag));
        }
    }
}