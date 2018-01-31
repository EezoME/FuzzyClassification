package edu.eezo.fzcl.fuzzyInference;

import edu.eezo.fzcl.entities.internal.LetterType;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.*;

/**
 * База знаний.
 */
@ManagedBean
@SessionScoped
public class KnowledgeBase implements Serializable {
    private Map<LetterType, Range> lettersRanges;
    private Map<String, List<Range>> wordsWeights;

    public void initLettersRanges(List<LetterType> allLetters) {
        if (allLetters == null) return;
        this.lettersRanges = new HashMap<>();
        int startPoint = 0;
        int endPoint = 99;
        for (LetterType letter : allLetters) {
            this.lettersRanges.put(letter, new Range(startPoint, endPoint));
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
                    addRangeToWord(ordinaryWord, this.lettersRanges.get(letterType).getHighSubrange());
                } else if (mostUsedWordsSet.contains(ordinaryWord) && letterType.equals(mostUsedWordsMap.get(ordinaryWord))) {
                    addRangeToWord(ordinaryWord, this.lettersRanges.get(letterType).getMiddleSubrange());
                } else {
                    addRangeToWord(ordinaryWord, this.lettersRanges.get(letterType).getLowSubrange());
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
        return lettersRanges;
    }

    public Map<String, List<Range>> getWordsWeights() {
        return wordsWeights;
    }

    class Range {
        private int startPoint;
        private int endPoint;

        public Range(int startPoint, int endPoint) {
            if (startPoint >= endPoint) return;
            this.startPoint = startPoint;
            this.endPoint = endPoint;
        }

        public Range getLowSubrange() {
            if (!isCorrect()) return null;
            int localInterval = this.endPoint - this.startPoint + 1;
            int high = (int) (localInterval * 0.5);
            return new Range(this.startPoint, this.startPoint + high);
        }

        public Range getMiddleSubrange() {
            if (!isCorrect()) return null;
            int localInterval = this.endPoint - this.startPoint + 1;
            int low = (int) (localInterval * 0.25);
            int high = (int) (localInterval * 0.75);
            return new Range(this.startPoint + low, this.startPoint + high);
        }

        public Range getHighSubrange() {
            if (!isCorrect()) return null;
            int localInterval = this.endPoint - this.startPoint + 1;
            int low = (int) (localInterval * 0.5);
            return new Range(this.startPoint + low, this.endPoint);
        }

        private boolean isCorrect() {
            return this.startPoint < this.endPoint;
        }

        @Override
        public String toString() {
            return "Range{" +
                    "startPoint=" + startPoint +
                    ", endPoint=" + endPoint +
                    '}';
        }
    }
}
