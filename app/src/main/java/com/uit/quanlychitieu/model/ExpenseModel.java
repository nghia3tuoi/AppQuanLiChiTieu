package com.uit.quanlychitieu.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.uit.quanlychitieu.MainActivity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseModel {
    private int expenseId;
    private int categoryId;
    private String expenseDate;
    private String expenseTime;
    private int expenseMoney;
    private String note;
    private int userId;

    public String categoryName;
    public String dateFormated;
    public String timeFormated;
    public String moneyFormated;
    public byte[] imgCategory;

    public Bitmap bitmap;

    @BindingAdapter("bind:imageBitmap")
    public static void loadImage(ImageView img, Bitmap bitmap) {
        img.setImageBitmap(bitmap);
    }

    private void formatData() {
        //Lấy tên danh mục dựa trên id của danh mục đó
        if (MainActivity.categoryExpanses != null) {
            for (CategoryModel category : MainActivity.categoryExpanses) {
                if (category.getCategoryId() == categoryId) {
                    categoryName = category.getName();
                    imgCategory = category.getImageCategory();
                    bitmap = BitmapFactory.decodeByteArray(imgCategory, 0, imgCategory.length);
                    break;
                }
            }
        }

        //Định dạng ngày tháng
        String sDate = expenseDate + "";
        Date date = new SimpleDateFormat("dd-MM-yyyy").parse(sDate, new ParsePosition(0));
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormated = dateFormat.format(date);

        //Định dạng thời gian
        String sTime = expenseTime + "";
        Date time = new SimpleDateFormat("hh:mm:ss").parse(sTime, new ParsePosition(0));
        DateFormat timeFormat = new SimpleDateFormat("hh:mm");
        timeFormated = timeFormat.format(time);

        //Định dạng tiền
        moneyFormated = "- " + NumberFormat.getCurrencyInstance().format(expenseMoney);
    }

    public ExpenseModel(int expenseId, int categoryId, String expenseDate, String expenseTime, int expenseMoney, String note, int userId) {
        this.expenseId = expenseId;
        this.categoryId = categoryId;
        this.expenseDate = expenseDate;
        this.expenseTime = expenseTime;
        this.expenseMoney = expenseMoney;
        this.note = note;
        this.userId = userId;

        formatData();
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getExpenseTime() {
        return expenseTime;
    }

    public void setExpenseTime(String expenseTime) {
        this.expenseTime = expenseTime;
    }

    public int getExpenseMoney() {
        return expenseMoney;
    }

    public void setExpenseMoney(int expenseMoney) {
        this.expenseMoney = expenseMoney;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
