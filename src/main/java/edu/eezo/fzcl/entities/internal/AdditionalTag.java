package edu.eezo.fzcl.entities.internal;

import java.util.HashMap;
import java.util.Map;

public class AdditionalTag {
    public static final String[] SUPPORTED_LOCALES = new String[]{"ru", "en", "ua"};
    private String tagName;
    private Map<String, String> localizedDescription;
    private Map<String, String> localizedExplanation;

    public AdditionalTag(String tagName) {
        this.tagName = tagName;
        this.localizedDescription = new HashMap<>();
        this.localizedExplanation = new HashMap<>();
    }

    public void addLocalizedDescription(String locale, String description) {
        this.localizedDescription.put(locale, description);
    }

    public void addLocalizedExplanation(String locale, String description) {
        this.localizedDescription.put(locale, description);
    }

    public String getTagName() {
        return tagName;
    }

    public String getLocalizedDescription(String locale) {
        return this.localizedDescription.get(locale);
    }
}
