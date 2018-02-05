package edu.eezo.fzcl.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import edu.eezo.fzcl.entities.internal.AdditionalTag;
import edu.eezo.fzcl.entities.internal.ContentAnalyzer;
import edu.eezo.fzcl.entities.internal.LetterType;
import edu.eezo.fzcl.entities.internal.Range;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * This class reads and parses files in <code>letters</code> directory.
 * <p id='basic'>Basically, letter reader has two read modes: <b>metadata-read mode</b> and <b>content-read mode</b>.
 * Metadata-read mode is set by default.</p>
 * <p id='content-read-mode'>Metadata-read mode reads head of file to identify letter type and additional info.</p>
 * <p name='content-read-mode'>Content-read mode sets on by the first semicolon(;).
 * Content-read mode activates {@link ContentAnalyzer} to count words in letters.<br/>
 * Second and subsequent semicolons just separate different letters in file.</p>
 */
@ManagedBean
@ApplicationScoped
public class LetterService implements Serializable {
    private static final String LETTERS_FOLDER_PATH = "D:\\Fuzzy Classification\\src\\main\\webapp\\resources\\letters\\";
    private List<AdditionalTag> additionalTags;
    private List<LetterType> letterTypes;
    private Map<String, List<LetterType>> commonWords;
    private Map<String, LetterType> mostUsedWordsByLetters;

    /**
     * Reads {@code info.json} file in letters directory.
     */
    @SuppressWarnings("unchecked")
    public void readInfo() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(LETTERS_FOLDER_PATH + "info.json"), "UTF-8"))) {
            Gson gson = new Gson();
            Map map = gson.fromJson(br, Map.class);
            List<Map> mapList = (List<Map>) map.get("additional-tags");
            additionalTags = new ArrayList<>();
            for (Map tag : mapList) {
                AdditionalTag additionalTag = new AdditionalTag((String) tag.get("tag"));
                for (String locale : AdditionalTag.SUPPORTED_LOCALES) {
                    if (tag.containsKey(locale)) {
                        additionalTag.addLocalizedDescription(locale, (String) tag.get(locale));
                        continue;
                    }
                    String explanation = "explanation-" + locale;
                    if (tag.containsKey(explanation)) {
                        additionalTag.addLocalizedExplanation(locale, (String) tag.get(explanation));
                    }
                }
                additionalTags.add(additionalTag);
            }
        } catch (FileNotFoundException e) {
            IndexController.showMessage("File not found: " + e.getLocalizedMessage());
        } catch (JsonSyntaxException e1) {
            System.err.println("Error in json file: " + e1.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read letter type files in letter directory.
     */
    public void readLetterTypes() {
        this.letterTypes = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(LETTERS_FOLDER_PATH))) {
            paths.filter(Files::isRegularFile).forEach(this::readAndParseLetterTypeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Searches and sets a set of unique words in each letter.
     */
    public void searchForUniqueWords() {
        for (LetterType letterType : letterTypes) {
            Set<String> filteredWords = new HashSet<>(letterType.getContentAnalyzer().getWordsCounterFiltered().keySet());

            for (LetterType letterType1 : letterTypes) {
                if (letterType.equals(letterType1)) continue;
                Set<String> filteredWordsCompared = letterType1.getContentAnalyzer().getWordsCounterFiltered().keySet();
                filteredWords.removeAll(filteredWordsCompared);
            }
            letterType.getContentAnalyzer().setUniqueWords(filteredWords);
        }
    }

    /**
     * Searches and sets common words in letters into map.
     */
    public void searchForCommonWords() {
        this.commonWords = new HashMap<>();
        for (LetterType firstCircleLetter : letterTypes) {
            Set<String> firstCircleNonUniqueWords = firstCircleLetter.getContentAnalyzer().getNotUniqueWords();

            for (String firstCircleNonUniqueWord : firstCircleNonUniqueWords) {
                if (this.commonWords.keySet().contains(firstCircleNonUniqueWord)) continue;
                List<LetterType> letterTypeList = new ArrayList<>();
                letterTypeList.add(firstCircleLetter);

                for (LetterType secondCircleLetter : letterTypes) {
                    if (firstCircleLetter.equals(secondCircleLetter)) continue;
                    Set<String> secondCircleNonUniqueWords = secondCircleLetter.getContentAnalyzer().getNotUniqueWords();

                    for (String secondCircleNonUniqueWord : secondCircleNonUniqueWords) {
                        if (firstCircleNonUniqueWord.equalsIgnoreCase(secondCircleNonUniqueWord)) {
                            letterTypeList.add(secondCircleLetter);
                        }
                    }
                }
                this.commonWords.put(firstCircleNonUniqueWord, letterTypeList);
            }
        }
    }

    /**
     * Searches and returns a map of words and letters, where they are used most.
     *
     * @return a map of words and letters
     */
    public Map<String, LetterType> getMostUsedWordsByLetters() {
        if (this.mostUsedWordsByLetters == null) {
            this.mostUsedWordsByLetters = new HashMap<>();
            Set<String> allWords = getAllWords();
            for (String word : allWords) {
                int max = Integer.MIN_VALUE;
                int index = -1;
                for (int i = 0; i < letterTypes.size(); i++) {
                    if (!letterTypes.get(i).getContentAnalyzer().getWordsCounterFiltered().containsKey(word)) continue;
                    Integer number = letterTypes.get(i).getContentAnalyzer().getWordsCounterFiltered().get(word);
                    if (number > max) {
                        max = number;
                        index = i;
                    }
                }
                if (index != -1) {
                    mostUsedWordsByLetters.put(word, letterTypes.get(index));
                }
            }
        }
        return mostUsedWordsByLetters;
    }

    public LetterType identifyLTByRange(Range range) {
        for (LetterType letterType : this.letterTypes) {
            if (letterType.isLTContainsRange(range)) {
                return letterType;
            }
        }
        return null;
    }

    public List<AdditionalTag> getAdditionalTags() {
        return additionalTags;
    }

    public List<LetterType> getLetterTypes() {
        return letterTypes;
    }

    public Map<String, List<LetterType>> getCommonWords() {
        return commonWords;
    }


    /**
     * Reads and parses letter type file.<br/>
     * Letter type file should have following syntax:<br/><br/>
     * <code>tags = [tag names, separated by commas]</code><br/>
     * <code>lang.locale = [localized description text]</code> (locale must be like 'en' format, see {@link AdditionalTag#SUPPORTED_LOCALES})<br/>
     * <code>additional-tags = [add. tags from info.json, separated by commas]</code>, see {@link LetterService#readInfo()}<br/>
     * <code>;</code> (first semicolon switch to content-read mode)<br/>
     * <code>[letter content]</code><br/>
     * <code>;</code> (second and subsequent semicolons separate different letters in file)<br/>
     *
     * @param path file path
     */
    private void readAndParseLetterTypeFile(Path path) {
        if (!path.toString().contains(".txt")) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()), "UTF-8"))) {
            String line;
            LetterType letterType = new LetterType();
            boolean isInContentReadMode = false; // default is Metadata-Read Mode
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                line = line.toLowerCase();
                if (!isInContentReadMode) { // Metadata-Read Mode
                    if (line.startsWith(";")) { // switch Content-Read Mode
                        letterType.incAnalyzedNumber();
                        isInContentReadMode = true;
                        continue;
                    }
                    if (line.startsWith("tags")) { // read tags line
                        letterType.addTag(line.split("=")[1].trim());
                        continue;
                    }
                    if (line.startsWith("lang")) { // read languages lines
                        letterType.addLocalizedDescription(line.split("\\.")[1].trim());
                        continue;
                    }
                    if (line.startsWith("additional-tags")) { // read additional tags line
                        letterType.addAdditionalTags(line.split("=")[1], additionalTags);
                    }
                } else { // Content-Read Mode
                    if (line.startsWith(";")) {
                        letterType.incAnalyzedNumber();
                        continue;
                    }
                    letterType.getContentAnalyzer().analyzeString(line);
                }
            }
            this.letterTypes.add(letterType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Searches and returns a set of all words from all analyzed letters.
     *
     * @return returns a set of all words
     */
    private Set<String> getAllWords() {
        Set<String> allWords = new HashSet<>();
        for (LetterType letterType : letterTypes) {
            allWords.addAll(letterType.getContentAnalyzer().getWordsCounterFiltered().keySet());
        }
        return allWords;
    }
}
