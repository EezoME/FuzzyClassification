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
import java.util.*;

@ManagedBean
@SessionScoped
public class ClassificationController implements Serializable {
    @ManagedProperty("#{knowledgeBase}")
    private KnowledgeBase knowledgeBase;
    @ManagedProperty("#{teachingController}")
    private TeachingController teachingController;
    private Part file;
    private Map<LetterType, Double> classificationResults;
    private Map<LetterType, Double> normalizedClassificationResults;
    private boolean isClassificated = false;

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
            normalizeClassificationResult();
        } else {
            System.out.println("Read from file.");
            try {
                String fileContent = new Scanner(file.getInputStream(), "UTF-8").useDelimiter("\\A\\n\\r").next();
                this.classificationResults = this.knowledgeBase.classificate(fileContent);
                normalizeClassificationResult();
            } catch (IOException e) {
                // Error handling
            }
        }
        isClassificated = true;
    }

    public void validateFile(FacesContext context, UIComponent component, Object value) {
        List<FacesMessage> messages = new ArrayList<>();
        Part file = (Part) value;
        if (file != null) {
//            if (file.getSize() > 1024) {
//                messages.add(new FacesMessage("file too big"));
//            }
            if (!"text/plain".equals(file.getContentType())) {
                messages.add(new FacesMessage("not a text file"));
            }
        }
        if (!messages.isEmpty()) {
            throw new ValidatorException(messages);
        }
    }

    public String getMostPossibleLetterType() {
        double max = Integer.MIN_VALUE;
        LetterType mostPossibleLetterType = new LetterType();
        for (LetterType letterType : this.classificationResults.keySet()) {
            if (Double.compare(this.classificationResults.get(letterType), max) == 1) {
                max = this.classificationResults.get(letterType);
                mostPossibleLetterType = letterType;
            }
        }
        return mostPossibleLetterType.getTags().get(0);
    }

    public String adjustDecision() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String adjustedLetterType = params.get("classificated:manual-selected-letter-type");
        LetterType adjustedLT = null;
        for (LetterType letterType : teachingController.getLetterService().getLetterTypes()) {
            if (letterType.getTags().get(0).equals(adjustedLetterType)) {
                adjustedLT = letterType;
            }
        }
        if (adjustedLT == null) return "";

        String letterTextareaContent = params.get("letter-form:letter-textfield").trim();
        if (this.file == null && letterTextareaContent.isEmpty()) {
            IndexController.showMessage("Letter text is not set.");
            return "";
        }
        if (!letterTextareaContent.isEmpty()) {
            adjustedLT.getContentAnalyzer().analyzeString(letterTextareaContent);
        } else {
            try {
                String fileContent = new Scanner(file.getInputStream(), "UTF-8").useDelimiter("\\A\\n\\r").next();
                adjustedLT.getContentAnalyzer().analyzeString(fileContent);
            } catch (IOException e) {
                // Error handling
            }
        }
        return "";
    }

    private void normalizeClassificationResult() {
        this.normalizedClassificationResults = new HashMap<>(this.classificationResults);
        double sum = 0;
        int nonZeroCounter = 0;
        for (LetterType letterType : this.classificationResults.keySet()) {
            double value = this.classificationResults.get(letterType);
            if (Double.compare(value, 0.0d) != 0) {
                nonZeroCounter++;
            }
            sum += value;
        }
        for (LetterType letterType : this.normalizedClassificationResults.keySet()) {
            this.normalizedClassificationResults.put(letterType, this.classificationResults.get(letterType) / sum);
        }
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
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

    public boolean isClassificated() {
        return isClassificated;
    }

    public void setClassificated(boolean classificated) {
        isClassificated = classificated;
    }

    public TeachingController getTeachingController() {
        return teachingController;
    }

    public void setTeachingController(TeachingController teachingController) {
        this.teachingController = teachingController;
    }

    public void setClassificationResults(Map<LetterType, Double> classificationResults) {
        this.classificationResults = classificationResults;
    }

    public Map<LetterType, Double> getNormalizedClassificationResults() {
        return normalizedClassificationResults;
    }

    public void setNormalizedClassificationResults(Map<LetterType, Double> normalizedClassificationResults) {
        this.normalizedClassificationResults = normalizedClassificationResults;
    }
}
