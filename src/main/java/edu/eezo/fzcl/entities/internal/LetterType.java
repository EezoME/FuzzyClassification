package edu.eezo.fzcl.entities.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LetterType {
    private static final String[] SUPPORTED_LOCALES = AdditionalTag.SUPPORTED_LOCALES;
    private static final String[] SUPPORTED_LOCALES_TESTS = new String[]{"RU: ", "EN: ", "UA: "};
    private List<String> tags;
    private Map<String, String> localizedDescription;
    private List<AdditionalTag> additionalTags;
    private ContentAnalyzer contentAnalyzer;
    private int analyzedNumber = 0;

    public LetterType() {
        this.tags = new ArrayList<>();
        this.localizedDescription = new HashMap<>();
        this.additionalTags = new ArrayList<>();
        this.contentAnalyzer = new ContentAnalyzer();
    }

    public LetterType(List<String> tags) {
        this.tags = tags;
        this.localizedDescription = new HashMap<>();
        this.additionalTags = new ArrayList<>();
        this.contentAnalyzer = new ContentAnalyzer();
    }

    public LetterType(String tag) {
        this.tags = new ArrayList<>();
        this.tags.add(tag);
        this.localizedDescription = new HashMap<>();
        this.additionalTags = new ArrayList<>();
        this.contentAnalyzer = new ContentAnalyzer();
    }

    public static String getSupportLocaleText(String locale) {
        for (int i = 0; i < SUPPORTED_LOCALES.length; i++) {
            if (SUPPORTED_LOCALES[i].equals(locale)) {
                return SUPPORTED_LOCALES_TESTS[i];
            }
        }
        return "UNKNOWN LOCALE: ";
    }


    public void addTag(String tag) {
        String[] splitedTags = tag.split(",");
        for (String singleTag : splitedTags) {
            this.tags.add(singleTag.trim());
        }
    }

    public void addLocalizedDescription(String localizedDescription) {
        String[] splitted = localizedDescription.split("=");
        String locale = splitted[0].trim();
        for (String supportedLocale : SUPPORTED_LOCALES) {
            if (locale.equals(supportedLocale)) {
                String description = splitted[1].trim();
                this.localizedDescription.put(locale, description);
            }
        }
    }

    public void addAdditionalTags(String additionals, List<AdditionalTag> additionalTagList) {
        String[] splittedTags = additionals.split(",");
        for (AdditionalTag additionalTag : additionalTagList) {
            for (String splittedTag : splittedTags) {
                if (additionalTag.getTagName().equals(splittedTag.trim())) {
                    this.additionalTags.add(additionalTag);
                }
            }
        }
    }

    public List<String> getTags() {
        return tags;
    }

    public Map<String, String> getLocalizedDescription() {
        return localizedDescription;
    }

    public List<AdditionalTag> getAdditionalTags() {
        return additionalTags;
    }

    public ContentAnalyzer getContentAnalyzer() {
        return contentAnalyzer;
    }

    public int getAnalyzedNumber() {
        return analyzedNumber;
    }

    public void incAnalyzedNumber() {
        this.analyzedNumber++;
    }

    @Override
    public String toString() {
        return "LetterType{" + tags.get(0) + "}";
    }
}
