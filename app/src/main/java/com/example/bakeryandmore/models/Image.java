package com.example.bakeryandmore.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Image implements Serializable {

    private String id;
    private String imageURL;
    private boolean isSelected;

    public Image() {
    }

    public Image(String id, String imageURL, Boolean isSelected) {
        this.id = id;
        this.imageURL = imageURL;
        this.isSelected = isSelected;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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
