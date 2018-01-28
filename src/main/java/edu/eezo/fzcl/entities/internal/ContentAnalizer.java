package edu.eezo.fzcl.entities.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ContentAnalizer {
    private Map<String, Integer> wordsCounter;

    public ContentAnalizer() {
        this.wordsCounter = new HashMap<>();
    }

    public void analizeString(String line) {
        line = line.toLowerCase();
        String[] splited = line.split("\\s|\\.|/|\\?|!|,|;|\\(|\\)|:");
        for (String word : splited) {
            if (word.length() < 4) continue;
            if (word.matches("[^а-яa-zіёї-]+")) continue;
            if (wordsCounter.containsKey(word)) {
                wordsCounter.put(word, wordsCounter.get(word) + 1);
            } else {
                wordsCounter.put(word, 1);
            }
        }
    }

    public Map<String, Integer> getWordsCounter() {
        return wordsCounter;
    }

    public Map<String, Integer> getWordsCounterFiltered() {
        Map<String, Integer> newWordsCounter = new HashMap<>();
        Set<String> words = this.wordsCounter.keySet();
        for (String word : words) {
            if (wordsCounter.get(word) > 2) {
                newWordsCounter.put(word, wordsCounter.get(word));
            }
        }
        return newWordsCounter;
    }
}
