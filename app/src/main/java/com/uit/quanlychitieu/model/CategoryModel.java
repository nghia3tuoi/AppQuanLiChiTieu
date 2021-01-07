package com.uit.quanlychitieu.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;


import com.uit.quanlychitieu.BR;
import com.uit.quanlychitieu.R;

import java.io.Serializable;

public class CategoryModel extends BaseObservable {
    private int categoryId;
    private String name;
    private String description;
    private byte[] imageCategory;

    @Bindable
    public Bitmap bitmap;

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        notifyPropertyChanged(BR.bitmap);
    }

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

        if (imageCategory != null) {
            bitmap = BitmapFactory.decodeByteArray(imageCategory, 0, imageCategory.length);
        }
    }

    @Bindable
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
        notifyPropertyChanged(BR.categoryId);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public byte[] getImageCategory() {
        return imageCategory;
    }

    public void setImageCategory(byte[] imageCategory) {
        this.imageCategory = imageCategory;
        notifyPropertyChanged(BR.imageCategory);
    }

}
