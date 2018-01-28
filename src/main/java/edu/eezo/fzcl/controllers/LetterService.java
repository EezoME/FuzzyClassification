package edu.eezo.fzcl.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import edu.eezo.fzcl.entities.internal.AdditionalTag;
import edu.eezo.fzcl.entities.internal.LetterType;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ManagedBean
@ApplicationScoped
public class LetterService {
    private static final String LETTERS_FOLDER_PATH = "F:\\Fuzzy Classification\\src\\main\\webapp\\resources\\letters\\";
    private List<AdditionalTag> additionalTags;
    private List<LetterType> letterTypes;

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
            paths.filter(Files::isRegularFile).forEach(this::readLetterTypeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readLetterTypeFile(Path path) {
        if (!path.toString().contains(".txt")) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()), "UTF-8"))) {
            String line = null;
            LetterType letterType = new LetterType();
            boolean isInContentReadMode = false; // default is Metadata-Read Mode
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (!isInContentReadMode) { // Metadata-Read Mode
                    if (line.startsWith(";")) { // switch Content-Read Mode
                        letterType.incAnalizedNumber();
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
                        letterType.incAnalizedNumber();
                        continue;
                    }
                    letterType.getContentAnalizer().analizeString(line);
                }
            }
            this.letterTypes.add(letterType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<AdditionalTag> getAdditionalTags() {
        return additionalTags;
    }

    public List<LetterType> getLetterTypes() {
        return letterTypes;
    }
}
