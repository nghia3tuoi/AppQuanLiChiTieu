package com.uit.quanlychitieu.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.Observable;

//import com.uit.quanlychitieu.BR;
import com.uit.quanlychitieu.MainActivity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class IncomeModel extends BaseObservable {
    private int incomeId;
    private int categoryId;
    private String incomeDate;
    private String incomeTime;
    private int incomeMoney;
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
        if (MainActivity.categoryIncomes != null) {
            for (CategoryModel category : MainActivity.categoryIncomes) {
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
        String sDate = incomeDate + "";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(sDate, new ParsePosition(0));
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormated = dateFormat.format(date);

        //Định dạng thời gian
        String sTime = incomeTime + "";
        Date time = new SimpleDateFormat("hh:mm:ss").parse(sTime, new ParsePosition(0));
        DateFormat timeFormat = new SimpleDateFormat("hh:mm");
        timeFormated = timeFormat.format(time);

        //Định dạng tiền
        Locale locale = new Locale.Builder().setLanguage("vi").setRegion("VN").build();
        NumberFormat currency = NumberFormat.getCurrencyInstance(locale);
        moneyFormated = "+ " + currency.format(incomeMoney);
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

    @Bindable
    public int getIncomeId() {
        return incomeId;
    }

    public void setIncomeId(int incomeId) {
        this.incomeId = incomeId;
        //notifyPropertyChanged(BR.incomeId);
    }

    @Bindable
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
        //notifyPropertyChanged(BR.categoryId);
    }

    @Bindable
    public String getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(String incomeDate) {
        this.incomeDate = incomeDate;
        //notifyPropertyChanged(BR.incomeDate);
    }

    @Bindable
    public String getIncomeTime() {
        return incomeTime;
    }

    public void setIncomeTime(String incomeTime) {
        this.incomeTime = incomeTime;
        //notifyPropertyChanged(BR.incomeTime);
    }

    @Bindable
    public int getIncomeMoney() {
        return incomeMoney;
    }

    public void setIncomeMoney(int incomeMoney) {
        this.incomeMoney = incomeMoney;
        //notifyPropertyChanged(BR.incomeMoney);
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
