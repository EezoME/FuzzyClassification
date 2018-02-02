package edu.eezo.fzcl.fuzzyInference;

import edu.eezo.fzcl.controllers.LetterService;
import edu.eezo.fzcl.entities.internal.LetterType;
import edu.eezo.fzcl.entities.internal.Range;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.*;

/**
 * База знаний.
 */
@ManagedBean
@SessionScoped
public class KnowledgeBase implements Serializable {
    @ManagedProperty("#{letterService}")
    private LetterService letterService;
    private Map<String, List<Range>> wordsWeights;

    public void initLettersRanges() {
        initLettersRanges(this.letterService.getLetterTypes());
    }

    public void initLettersRanges(List<LetterType> allLetters) {
        if (allLetters == null) return;
        int startPoint = 0;
        int endPoint = 99;
        for (LetterType letter : allLetters) {
            letter.setRange(new Range(startPoint, endPoint));
            startPoint += 100;
            endPoint += 100;
        }
    }

    public void initWordsWeights(List<LetterType> allLetters, Map<String, LetterType> mostUsedWordsMap) {
        if (allLetters == null || mostUsedWordsMap == null) return;
        this.wordsWeights = new HashMap<>();
        for (LetterType letterType : allLetters) {
            Set<String> ordinaryWordsSet = letterType.getContentAnalyzer().getWordsCounterFiltered().keySet();
            Set<String> mostUsedWordsSet = mostUsedWordsMap.keySet();
            Set<String> uniqueWordsSet = letterType.getContentAnalyzer().getUniqueWords();
            for (String ordinaryWord : ordinaryWordsSet) {
                if (uniqueWordsSet.contains(ordinaryWord)) {
                    addRangeToWord(ordinaryWord, letterType.getRange().getHighSubrange());
                } else if (mostUsedWordsSet.contains(ordinaryWord) && letterType.equals(mostUsedWordsMap.get(ordinaryWord))) {
                    addRangeToWord(ordinaryWord, letterType.getRange().getMiddleSubrange());
                } else {
                    addRangeToWord(ordinaryWord, letterType.getRange().getLowSubrange());
                }
            }
        }
    }

    private void addRangeToWord(String word, Range range) {
        if (this.wordsWeights.containsKey(word)) {
            this.wordsWeights.get(word).add(range);
        } else {
            List<Range> ranges = new ArrayList<>();
            ranges.add(range);
            this.wordsWeights.put(word, ranges);
        }
    }

    public Map<LetterType, Range> getLettersRanges() {
        Map<LetterType, Range> lettersRanges = new HashMap<>();
        for (LetterType letterType : letterService.getLetterTypes()) {
            lettersRanges.put(letterType, letterType.getRange());
        }
        return lettersRanges;
    }

    public Map<String, List<Range>> getWordsWeights() {
        return wordsWeights;
    }

    public LetterService getLetterService() {
        return letterService;
    }

    public void setLetterService(LetterService letterService) {
        this.letterService = letterService;
    }
}
