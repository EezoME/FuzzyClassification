package edu.eezo.fzcl.controllers;

import edu.eezo.fzcl.fuzzyInference.KnowledgeBase;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@ManagedBean
@Named
@SessionScoped
public class TeachingController implements Serializable {
    @ManagedProperty("#{letterService}")
    private LetterService letterService;
    @ManagedProperty("#{knowledgeBase}")
    private KnowledgeBase knowledgeBase;

    @PostConstruct
    public void init() {
        letterService.readInfo();
        letterService.readLetterTypes();
        letterService.searchForUniqueWords();
        letterService.searchForCommonWords();
        knowledgeBase.initLettersRanges();
        knowledgeBase.initWordsWeights(letterService.getLetterTypes(), letterService.getMostUsedWordsByLetters());
    }

    public LetterService getLetterService() {
        return letterService;
    }

    public void setLetterService(LetterService letterService) {
        this.letterService = letterService;
    }

    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }
}
