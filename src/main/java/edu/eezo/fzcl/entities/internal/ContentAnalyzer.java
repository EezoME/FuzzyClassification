package edu.eezo.fzcl.entities.internal;

import java.util.*;

public class ContentAnalyzer {
    /**
     * Map of words and their number in the letter.
     */
    private Map<String, Integer> wordsCounter;
    /**
     * Map of filtered words and their number in the letter.
     * Filters all the words, which in a letter at least three
     */
    private Map<String, Integer> wordsCounterFiltered;
    private Set<String> uniqueWords;

    ContentAnalyzer() {
        this.wordsCounter = new HashMap<>();
        this.uniqueWords = new HashSet<>();
    }

    // TODO: найти более подходяшие примеры писем
    // TODO: найти способ выделять однокоренные слова

    /**
     * Analyzes words in line.<br/>
     * This method avoid words with length less than 4 characters and
     * with non-alphabetic character (EN, RU, UA chars counts).
     *
     * @param line a line to analyze
     */
    public void analyzeString(String line) {
        line = line.toLowerCase();
        String[] splitted = line.split("\\s|\\.|/|\\?|!|,|;|\\(|\\)|:|-");
        for (String word : splitted) {
            if (word.length() < 4) continue;
            if (word.matches("[^а-яa-zіёї]+")) continue;
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

    /**
     * Returns a asymmetric set difference of the two sets: {@link #wordsCounterFiltered} and {@link #uniqueWords}.
     *
     * @return a set of not unique words
     */
    public Set<String> getNotUniqueWords() {
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
