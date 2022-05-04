package com.tungnguyen.appbytung;

public class Camera {
    private String description;
    private String imageURL;

    public Camera(String description, String imageURL) {
        this.description = description;
        this.imageURL = imageURL;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }
}
