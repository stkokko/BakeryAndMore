package com.example.bakeryandmore.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Image implements Serializable {

    private String id;
    private String imageURL;

    public Image() {
    }

    public Image(String id, String imageURL) {
        this.id = id;
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public String toString() {
        return "Image{" +
                "id='" + id + '\'' +
                ", imageURL='" + imageURL + '\'' +
                '}';
    }
}
