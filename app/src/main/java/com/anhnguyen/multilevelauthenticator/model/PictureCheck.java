package com.anhnguyen.multilevelauthenticator.model;

public class PictureCheck {
    private String pathPicture;
    private boolean isUserUpload;

    public PictureCheck(String pathPicture, boolean isUser) {
        this.pathPicture = pathPicture;
        this.isUserUpload = isUser;
    }

    public PictureCheck() {
    }

    public String getPathPicture() {
        return pathPicture;
    }

    public void setPathPicture(String pathPicture) {
        this.pathPicture = pathPicture;
    }

    public boolean isUserUpload() {
        return isUserUpload;
    }

    public void setUserUpload(boolean userUpload) {
        isUserUpload = userUpload;
    }
}
