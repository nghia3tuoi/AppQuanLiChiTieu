package com.uit.quanlychitieu.ui.statistic.data_statistic;

import android.Manifest;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.databinding.ObservableArrayList;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.ExpenseModel;
import com.uit.quanlychitieu.ui.login.LoginActivity;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataStatisticModel {

    private String fromDate, endDate;
    private SQLiteDatabase database;
    private int USER_ID;

    public DataStatisticModel(String fromDate, String toDate) {
        database = MainActivity.database;
        USER_ID = MainActivity.USER_ID;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = new SimpleDateFormat("dd-MM-yyyy").parse(fromDate, new ParsePosition(0));
        Date d2 = new SimpleDateFormat("dd-MM-yyyy").parse(toDate, new ParsePosition(0));
        this.fromDate = format.format(d1);
        this.endDate = format.format(d2);
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public long getSumMoneyExpense() {
        Cursor cursor = database.rawQuery("select sum(ExpenseMoney) as Sum from ChiTieu where UserId = " + USER_ID +
                " and ExpenseDate >= '" + fromDate + "' and ExpenseDate <= '" + endDate + "'", null);
        long totalMoney = 0;
        while (cursor.moveToNext()) {
            try {
                String sTotalMoney = cursor.getString(0);
                totalMoney = Long.parseLong(sTotalMoney);
            } catch (Exception ex) {
                return 0;
            }
        }
        cursor.close();
        return totalMoney;
    }

    public long getSumMoneyIncome() {
        Cursor cursor = database.rawQuery("select sum(IncomeMoney) as Sum from ThuNhap where UserId = " + USER_ID +
                " and IncomeDate >= '" + fromDate + "' and IncomeDate <= '" + endDate + "'", null);
        long totalMoney = 0;
        while (cursor.moveToNext()) {
            try {
                String sTotalMoney = cursor.getString(0);
                totalMoney = Long.parseLong(sTotalMoney);
            } catch (Exception ex) {
                return 0;
            }
        }
        cursor.close();
        return totalMoney;
    }
}
