package com.uit.quanlychitieu.model;

import java.io.Serializable;

public class CategoryModel implements Serializable {
    private int categoryId;
    private String name;
    private String description;
    private byte[] imageCategory;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryModel() {
    }

    public CategoryModel(int categoryId, String name, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
    }

    public CategoryModel(int categoryId, String name, String description, byte[] imageCategory) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.imageCategory = imageCategory;
    }


    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImageCategory() {
        return imageCategory;
    }

    public void setImageCategory(byte[] imageCategory) {
        this.imageCategory = imageCategory;
    }

}
