package edu.eezo.fzcl.controllers;

import edu.eezo.fzcl.entities.internal.LetterType;
import edu.eezo.fzcl.fuzzyInference.KnowledgeBase;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Path;
import java.util.*;

@ManagedBean
@SessionScoped
public class ClassificationController implements Serializable {
    @ManagedProperty("#{knowledgeBase}")
    private KnowledgeBase knowledgeBase;
    private Path file;
    private Map<LetterType, Double> classificationResults;

    public void classificateActionListener() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String letterTextareaContent = params.get("letter-form:letter-textfield").trim();
        if (this.file == null && letterTextareaContent.isEmpty()) {
            IndexController.showMessage("Letter text is not set.");
            return;
        }
        if (!letterTextareaContent.isEmpty()) {
            System.out.println("Read form textarea.");
            this.classificationResults = this.knowledgeBase.classificate(letterTextareaContent);
        } else {
            System.out.println("Read from file.");
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file.toFile()))) {
                StringBuilder stringBuilder = new StringBuilder("");
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                this.classificationResults = this.knowledgeBase.classificate(stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void validateFile(FacesContext context, UIComponent component, Object value) {
        List<FacesMessage> messages = new ArrayList<>();
        Part file = (Part) value;
        if (file.getSize() > 1024) {
            messages.add(new FacesMessage("file too big"));
        }
        if (!"text/plain".equals(file.getContentType())) {
            messages.add(new FacesMessage("not a text file"));
        }
        if (!messages.isEmpty()) {
            throw new ValidatorException(messages);
        }
    }

    public String getMostPossibleLetterType() {
        double max = Integer.MIN_VALUE;
        LetterType mostPossibleLetterType = new LetterType();
        for (LetterType letterType : this.classificationResults.keySet()) {
            if (Double.compare(this.classificationResults.get(letterType),max) == 1) {
                max = this.classificationResults.get(letterType);
                mostPossibleLetterType = letterType;
            }
        }
        return mostPossibleLetterType.getTags().get(0);
    }

    public Path getFile() {
        return file;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    public Map<LetterType, Double> getClassificationResults() {
        return classificationResults;
    }
}
