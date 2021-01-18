package com.uit.quanlychitieu.ui.income;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.ExpenseModel;
import com.uit.quanlychitieu.model.IncomeModel;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IncomeViewModel extends ViewModel {

    MutableLiveData<List<IncomeModel>> listIncomeLiveData;
    List<IncomeModel> listIncome;

    MutableLiveData<List<IncomeModel>> listIncomeFilteredLiveData;
    List<IncomeModel> listIncomeFiltered;
    SQLiteDatabase database;
    int USER_ID;

    public IncomeViewModel() {
        database = MainActivity.database;
        USER_ID = MainActivity.USER_ID;
        listIncomeLiveData = new MutableLiveData<>();
        listIncomeFilteredLiveData = new MutableLiveData<>();
        initData();
    }

    private void initData() {
        listIncome = new ArrayList<>(MainActivity.incomes);
        listIncomeFiltered = new ArrayList<>(MainActivity.incomes);

        listIncomeLiveData.setValue(listIncome);
        listIncomeFilteredLiveData.setValue(listIncome);
    }

    public MutableLiveData<List<IncomeModel>> getListIncomeLiveData() {
        return listIncomeLiveData;
    }

    public MutableLiveData<List<IncomeModel>> getListIncomeFilteredLiveData() {
        return listIncomeFilteredLiveData;
    }

    public boolean addIncome(int categoryId, String date, String time, int money, String note) {

        Date d = new SimpleDateFormat("dd-MM-yyyy").parse(date, new ParsePosition(0));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = dateFormat.format(d);

        int expenseid = listIncome.get(listIncome.size() - 1).getIncomeId() + 1;
        IncomeModel income = new IncomeModel(expenseid, categoryId, formatDate, time + ":00", money, note, MainActivity.USER_ID);
        listIncome.add(income);
        MainActivity.incomes.add(income);
        listIncomeLiveData.setValue(listIncome);
        listIncomeFilteredLiveData.setValue(listIncome);

        //thêm dữ liệu vào database
        ContentValues content = new ContentValues();
        content.put("CategoryId", categoryId);
        content.put("IncomeDate", formatDate);
        content.put("IncomeTime", time + ":00");
        content.put("IncomeMoney", money);
        content.put("Note", note);
        content.put("UserId", USER_ID);

        long result = database.insert("ThuNhap", null, content);
        return result > 0;
    }

    public void deleteIncome(int position) {
        IncomeModel income = listIncome.get(position);
        listIncome.remove(position);
        MainActivity.incomes.remove(position);
        listIncomeLiveData.setValue(listIncome);
        listIncomeFilteredLiveData.setValue(listIncome);

        //xóa dữ liệu trong database
        int incomeId = income.getIncomeId();
        database.delete("ThuNhap", "IncomeId = ?", new String[]{String.valueOf(incomeId)});
    }

    public boolean updateIncome(int incomeId, int categoryId, String date, String time, int money, String note) {

        Date d = new SimpleDateFormat("dd-MM-yyyy").parse(date, new ParsePosition(0));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = dateFormat.format(d);

        for (IncomeModel income : listIncome) {
            if (income.getIncomeId() == incomeId && income.getUserId() == USER_ID) {
                income.setCategoryId(categoryId);
                income.setIncomeDate(formatDate);
                income.setIncomeTime(time + ":00");
                income.setIncomeMoney(money);
                income.setNote(note);
                income.formatData();
                break;
            }
        }
        listIncomeLiveData.setValue(listIncome);
        listIncomeFilteredLiveData.setValue(listIncome);

        //cập nhật dữ liệu xuống database
        ContentValues content = new ContentValues();
        content.put("CategoryId", categoryId);
        content.put("IncomeDate", formatDate);
        content.put("IncomeTime", time + ":00");
        content.put("IncomeMoney", money);
        content.put("Note", note);

        String sIncomeId = String.valueOf(incomeId);
        long result = database.update("ThuNhap", content, "IncomeId = ?", new String[]{sIncomeId});
        return result > 0;
    }

    public void updateFilter(String charString) {
        listIncomeFiltered.clear();
        if (charString.isEmpty()) {
            listIncomeFiltered.addAll(listIncome);
            listIncomeFilteredLiveData.setValue(listIncome);
            return;
        }
        for (IncomeModel income : listIncome) {
            String str1 = String.valueOf(income.categoryName).toLowerCase();
            String str2 = charString.toLowerCase();

            Integer money = tryParse(str2);

            if (str1.contains(str2) ||
                    str1.equals(str2) ||
                    (money != null && money == income.getIncomeMoney()) ||
                    income.getNote().toLowerCase().contains(str2)) {
                listIncomeFiltered.add(income);
            }
        }
        listIncomeFilteredLiveData.setValue(listIncomeFiltered);
    }

    public void cancelFilter() {
        listIncomeFilteredLiveData.setValue(listIncome);
    }

    public Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}