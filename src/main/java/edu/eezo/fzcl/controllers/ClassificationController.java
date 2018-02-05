package edu.eezo.fzcl.controllers;

import edu.eezo.fzcl.fuzzyInference.KnowledgeBase;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class ClassificationController implements Serializable {
    @ManagedProperty("#{knowledgeBase}")
    private KnowledgeBase knowledgeBase;
    private Path file;

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
}
