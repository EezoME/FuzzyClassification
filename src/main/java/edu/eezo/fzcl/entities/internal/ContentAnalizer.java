package edu.eezo.fzcl.entities.internal;

import java.util.*;

public class ContentAnalizer {
    private Map<String, Integer> wordsCounter;
    private Map<String, Integer> wordsCounterFiltered;
    private Set<String> uniqueWords;

    public ContentAnalizer() {
        this.wordsCounter = new HashMap<>();
        this.uniqueWords = new HashSet<>();
    }

    // TODO: найти более подходяшие примеры писем
    // TODO: найти способ выделять однокоренные слова
    // TODO: найти уникальные слова для каждого типа письма
    // TODO: найти общие слова для нескольких/всех типов писем

    /**
     * Analizes words in line.<br/>
     * This method avoid words with length less than 4 characters and
     * with non-alphabetic character (EN, RU, UA chars counts).
     *
     * @param line a line to analyze
     */
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
        if (this.wordsCounterFiltered == null) {
            this.wordsCounterFiltered = new HashMap<>();
            Set<String> words = this.wordsCounter.keySet();
            for (String word : words) {
                if (wordsCounter.get(word) > 2) {
                    this.wordsCounterFiltered.put(word, wordsCounter.get(word));
                }
            }
        }
        return this.wordsCounterFiltered;
    }

    public Set<String> getNonUniqueWords() {
        Set<String> allWords = new HashSet<>(wordsCounterFiltered.keySet());
        allWords.removeAll(uniqueWords);
        return allWords;
    }

    public Set<String> getUniqueWords() {
        return uniqueWords;
    }

    public void setUniqueWords(Set<String> uniqueWords) {
        this.uniqueWords = uniqueWords;
    }
}
