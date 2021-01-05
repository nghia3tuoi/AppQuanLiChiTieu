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

public class IncomeModel implements Serializable {
    private int incomeId;
    private int categoryId;
    private String incomeDate;
    private String incomeTime;
    private int incomeMoney;
    private String note;
    private int userId;

    public String categoryName;
    public String dateFormated;
    public String timeFormated;
    public String moneyFormated;
    public byte[] imgCategory;

    public Bitmap bitmap;

    @BindingAdapter("bind:imageBitmap")
    public static void loadImage(ImageView iv, Bitmap bitmap) {
        iv.setImageBitmap(bitmap);
    }

    private void formatData() {
        //Lấy tên danh mục dựa trên id của danh mục đó
        if (MainActivity.categoryIncomes != null) {
            for (CategoryModel category : MainActivity.categoryIncomes) {
                if (category.getCategoryId() == categoryId) {
                    categoryName = category.getName();
                    imgCategory = category.getImageCategory();
                    bitmap = BitmapFactory.decodeByteArray(imgCategory, 0, imgCategory.length);
                    break;
                }
            }
        }

        //Định dạng ngày tháng
        String sDate = incomeDate + "";
        Date date = new SimpleDateFormat("dd-MM-yyyy").parse(sDate, new ParsePosition(0));
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormated = dateFormat.format(date);

        //Định dạng thời gian
        String sTime = incomeTime + "";
        Date time = new SimpleDateFormat("hh:mm:ss").parse(sTime, new ParsePosition(0));
        DateFormat timeFormat = new SimpleDateFormat("hh:mm");
        timeFormated = timeFormat.format(time);

        //Định dạng tiền
        moneyFormated = "+ " + NumberFormat.getCurrencyInstance().format(incomeMoney);
    }

    public IncomeModel(int incomeId, int categoryId, String incomeDate, String incomeTime, int incomeMoney, String note, int userId) {
        this.incomeId = incomeId;
        this.categoryId = categoryId;
        this.incomeDate = incomeDate;
        this.incomeTime = incomeTime;
        this.incomeMoney = incomeMoney;
        this.note = note;
        this.userId = userId;

        formatData();
    }

    public int getIncomeId() {
        return incomeId;
    }

    public void setIncomeId(int incomeId) {
        this.incomeId = incomeId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(String incomeDate) {
        this.incomeDate = incomeDate;
    }

    public String getIncomeTime() {
        return incomeTime;
    }

    public void setIncomeTime(String incomeTime) {
        this.incomeTime = incomeTime;
    }

    public int getIncomeMoney() {
        return incomeMoney;
    }

    public void setIncomeMoney(int incomeMoney) {
        this.incomeMoney = incomeMoney;
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
