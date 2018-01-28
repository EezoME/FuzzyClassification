package edu.eezo.fzcl.controllers;

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
    private LetterService service;

    @PostConstruct
    public void init() {
        service.readInfo();
        service.readLetterTypes();
    }

    public LetterService getService() {
        return service;
    }

    public void setService(LetterService service) {
        this.service = service;
    }
}
