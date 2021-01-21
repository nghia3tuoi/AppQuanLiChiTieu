package com.uit.quanlychitieu.ui.user;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.MainActivity;

import java.text.NumberFormat;

public class UserViewModel extends ViewModel {

    SQLiteDatabase database;
    long moneyExpense;
    MutableLiveData<String> totalMoneyExpense;

    long moneyIncome;
    MutableLiveData<String> totalMoneyIncome;
    int USER_ID;

    public UserViewModel() {

        database = MainActivity.database;
        USER_ID = MainActivity.USER_ID;
        totalMoneyExpense = new MutableLiveData<>();
        totalMoneyIncome = new MutableLiveData<>();

        initData();
    }

    private void initData() {
        moneyExpense = totalMoneyExpenseFromDatabase();
        totalMoneyExpense.setValue(NumberFormat.getCurrencyInstance().format(moneyExpense));

        moneyIncome = totalMoneyIncomeFromDatabase();
        totalMoneyIncome.setValue(NumberFormat.getCurrencyInstance().format(moneyIncome));
    }

    public MutableLiveData<String> getTotalMoneyExpense() {
        return totalMoneyExpense;
    }

    public MutableLiveData<String> getTotalMoneyIncome() {
        return totalMoneyIncome;
    }

    private long totalMoneyExpenseFromDatabase() {
        Cursor cursor = database.rawQuery("select sum(ExpenseMoney) as Sum from ChiTieu where UserId = " + MainActivity.USER_ID, null);
        String sTotalMoney = "";
        while (cursor.moveToNext()) {
            sTotalMoney = cursor.getString(0);
        }
        cursor.close();
        try {
            long totalMoney = Long.parseLong(sTotalMoney);
            return totalMoney;
        } catch (Exception ex) {
            return 0;
        }
    }

    private long totalMoneyIncomeFromDatabase() {
        Cursor cursor = database.rawQuery("select sum(IncomeMoney) as Sum from ThuNhap where UserId = " + MainActivity.USER_ID, null);
        String sTotalMoney = "";
        while (cursor.moveToNext()) {
            sTotalMoney = cursor.getString(0);
        }
        cursor.close();
        try {
            long totalMoney = Long.parseLong(sTotalMoney);
            return totalMoney;
        } catch (Exception ex) {
            return 0;
        }
    }

    public void clearDataUser() {
        MainActivity.expenses.clear();
        MainActivity.incomes.clear();

        totalMoneyExpense.setValue(NumberFormat.getCurrencyInstance().format(0));
        totalMoneyIncome.setValue(NumberFormat.getCurrencyInstance().format(0));

        //xóa dữ liệu trong database
        String sUserId = String.valueOf(USER_ID);
        database.delete("ChiTieu", "UserId = ?", new String[]{String.valueOf(sUserId)});
        database.delete("ThuNhap", "UserId = ?", new String[]{String.valueOf(sUserId)});
    }
}