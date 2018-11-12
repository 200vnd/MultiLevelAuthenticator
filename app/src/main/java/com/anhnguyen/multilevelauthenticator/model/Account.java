package com.anhnguyen.multilevelauthenticator.model;

import java.io.Serializable;

public class Account implements Serializable {
    private String id;
    private String textPass;
    private String pattern;

    public Account() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTextPass() {
        return textPass;
    }

    public void setTextPass(String textPass) {
        this.textPass = textPass;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
