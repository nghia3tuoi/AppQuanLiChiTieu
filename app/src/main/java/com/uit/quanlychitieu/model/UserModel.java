package com.uit.quanlychitieu.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.uit.quanlychitieu.MainActivity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserModel implements Serializable {
    private int userId;
    private String displayName;
    private String userName;
    private String password;
    private String email;
    private byte[] imageAvatar;
    private String dateAdd;
    private String dateModify;

    public String dateAddFormated;
    public String dateModifyFormated;
    public Bitmap bitmap;

    private void formatData() {
        if (imageAvatar != null) {
            bitmap = BitmapFactory.decodeByteArray(imageAvatar, 0, imageAvatar.length);
        }

        String sDateAdd = dateAdd + "";
        String sDateModify = dateAdd + "";
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        //Định dạng ngày tháng
        Date dateAdd = new SimpleDateFormat("dd/MM/yyyy").parse(sDateAdd, new ParsePosition(0));
        dateAddFormated = dateFormat.format(dateAdd);

        //Định dạng ngày tháng
        Date dateModify = new SimpleDateFormat("dd/MM/yyyy").parse(sDateModify, new ParsePosition(0));
        dateModifyFormated = dateFormat.format(dateModify);
    }

    @BindingAdapter("bind:imageBitmap")
    public static void loadImage(ImageView iv, Bitmap bitmap) {
        iv.setImageBitmap(bitmap);
    }

    public UserModel(int userId, String displayName, String userName, String password, String email, byte[] imageAvatar, String dateAdd, String dateModify) {
        this.userId = userId;
        this.displayName = displayName;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.imageAvatar = imageAvatar;
        this.dateAdd = dateAdd;
        this.dateModify = dateModify;

        formatData();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getImageAvatar() {
        return imageAvatar;
    }

    public void setImageAvatar(byte[] imageAvatar) {
        this.imageAvatar = imageAvatar;
    }

    public String getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(String dateAdd) {
        this.dateAdd = dateAdd;
    }

    public String getDateModify() {
        return dateModify;
    }

    public void setDateModify(String dateModify) {
        this.dateModify = dateModify;
    }
}
