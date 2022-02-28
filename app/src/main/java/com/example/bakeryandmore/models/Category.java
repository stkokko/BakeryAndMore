package com.example.bakeryandmore.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {

    private String name;
    private Image categoryImage;
    private ArrayList<Image> images;

    public Category() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(Image categoryImage) {
        this.categoryImage = categoryImage;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", categoryImage=" + categoryImage +
                ", images=" + images +
                '}';
    }
}
