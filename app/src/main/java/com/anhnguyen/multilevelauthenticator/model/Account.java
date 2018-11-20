package com.anhnguyen.multilevelauthenticator.model;

import java.io.Serializable;

public class Account implements Serializable {
    private String id;
    private String textPass;
    private String pattern;
    private int picture;

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

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }
}
