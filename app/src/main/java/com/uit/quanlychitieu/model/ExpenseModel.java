package com.uit.quanlychitieu.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

//import com.uit.quanlychitieu.BR;
import com.uit.quanlychitieu.MainActivity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseModel extends BaseObservable {
    private int expenseId;
    private int categoryId;
    private String expenseDate;
    private String expenseTime;
    private int expenseMoney;
    private String note;
    private int userId;

    @Bindable
    public String categoryName;

    @Bindable
    public String dateFormated;

    @Bindable
    public String timeFormated;

    @Bindable
    public String moneyFormated;

    @Bindable
    public byte[] imgCategory;

    @Bindable
    public Bitmap bitmap;

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        //notifyPropertyChanged(BR.categoryName);
    }

    public void setDateFormated(String dateFormated) {
        this.dateFormated = dateFormated;
        //notifyPropertyChanged(BR.dateFormated);
    }

    public void setTimeFormated(String timeFormated) {
        this.timeFormated = timeFormated;
        //notifyPropertyChanged(BR.timeFormated);
    }

    public void setMoneyFormated(String moneyFormated) {
        this.moneyFormated = moneyFormated;
        //notifyPropertyChanged(BR.moneyFormated);
    }

    public void setImgCategory(byte[] imgCategory) {
        this.imgCategory = imgCategory;
        //notifyPropertyChanged(BR.imgCategory);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        //notifyPropertyChanged(BR.bitmap);
    }

    public void formatData() {
        //Lấy tên danh mục dựa trên id của danh mục đó
        if (MainActivity.categoryExpanses != null) {
            for (CategoryModel category : MainActivity.categoryExpanses) {
                if (category.getCategoryId() == categoryId) {
                    categoryName = category.getName();
                    imgCategory = category.getImageCategory();
                    if (imgCategory != null) {
                        bitmap = BitmapFactory.decodeByteArray(imgCategory, 0, imgCategory.length);
                    }
                    break;
                }
            }
        }

        //Định dạng ngày tháng
        String sDate = expenseDate + "";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(sDate, new ParsePosition(0));
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

    @Bindable
    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
        //notifyPropertyChanged(BR.expenseId);
    }

    @Bindable
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
        //notifyPropertyChanged(BR.expenseId);
    }

    @Bindable
    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
        //notifyPropertyChanged(BR.expenseDate);
    }

    @Bindable
    public String getExpenseTime() {
        return expenseTime;
    }

    public void setExpenseTime(String expenseTime) {
        this.expenseTime = expenseTime;
       // notifyPropertyChanged(BR.expenseTime);
    }

    @Bindable
    public int getExpenseMoney() {
        return expenseMoney;
    }

    public void setExpenseMoney(int expenseMoney) {
        this.expenseMoney = expenseMoney;
        //notifyPropertyChanged(BR.expenseMoney);
    }

    @Bindable
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
        //notifyPropertyChanged(BR.note);
    }

    @Bindable
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
        //notifyPropertyChanged(BR.userId);
    }
}
