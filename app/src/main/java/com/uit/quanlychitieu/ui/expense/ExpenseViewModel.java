package com.uit.quanlychitieu.ui.expense;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.model.ExpenseModel;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

public class ExpenseViewModel extends ViewModel {

    MutableLiveData<List<ExpenseModel>> listExpenseLiveData;
    List<ExpenseModel> listExpense;

    MutableLiveData<List<ExpenseModel>> listExpenseFilteredLiveData;
    List<ExpenseModel> listExpenseFiltered;
    SQLiteDatabase database;
    int USER_ID;

    public ExpenseViewModel() {
        database = MainActivity.database;
        USER_ID = MainActivity.USER_ID;
        listExpenseLiveData = new MutableLiveData<>();
        listExpenseFilteredLiveData = new MutableLiveData<>();
        initData();
    }

    private void initData() {
        listExpense = new ArrayList<>(MainActivity.expenses);
        listExpenseFiltered = new ArrayList<>(MainActivity.expenses);

        listExpenseLiveData.setValue(listExpense);
        listExpenseFilteredLiveData.setValue(listExpense);
    }

    public MutableLiveData<List<ExpenseModel>> getListExpenseLiveData() {
        return listExpenseLiveData;
    }

    public MutableLiveData<List<ExpenseModel>> getListExpenseFilteredLiveData() {
        return listExpenseFilteredLiveData;
    }

    public boolean addExpense(int categoryId, String date, String time, int money, String note) {

        Date d = new SimpleDateFormat("dd-MM-yyyy").parse(date, new ParsePosition(0));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = dateFormat.format(d);

        int expenseid = listExpense.get(listExpense.size() - 1).getExpenseId() + 1;
        ExpenseModel expense = new ExpenseModel(expenseid, categoryId, formatDate, time + ":00", money, note, USER_ID);
        listExpense.add(expense);
        MainActivity.expenses.add(expense);
        listExpenseLiveData.setValue(listExpense);
        listExpenseFilteredLiveData.setValue(listExpense);


        //thêm dữ liệu vào database
        ContentValues content = new ContentValues();
        content.put("CategoryId", categoryId);
        content.put("ExpenseDate", formatDate);
        content.put("ExpenseTime", time + ":00");
        content.put("ExpenseMoney", money);
        content.put("Note", note);
        content.put("UserId", USER_ID);

        long result = database.insert("ChiTieu", null, content);
        return result > 0;
    }

    public void deleteExpense(int position) {
        ExpenseModel expense = listExpense.get(position);
        listExpense.remove(position);
        MainActivity.expenses.remove(position);
        listExpenseLiveData.setValue(listExpense);
        listExpenseFilteredLiveData.setValue(listExpense);

        //xóa dữ liệu trong database
        int expenseId = expense.getExpenseId();
        database.delete("ChiTieu", "ExpenseId = ?", new String[]{String.valueOf(expenseId)});
    }

    public boolean updateExpense(int expenseId, int categoryId, String date, String time, int money, String note) {

        Date d = new SimpleDateFormat("dd-MM-yyyy").parse(date, new ParsePosition(0));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = dateFormat.format(d);

        for (ExpenseModel expense : listExpense) {
            if (expense.getExpenseId() == expenseId && expense.getUserId() == MainActivity.USER_ID) {
                expense.setCategoryId(categoryId);
                expense.setExpenseDate(formatDate);
                expense.setExpenseTime(time + ":00");
                expense.setExpenseMoney(money);
                expense.setNote(note);
                expense.formatData();
                break;
            }
        }
        listExpenseLiveData.setValue(listExpense);
        listExpenseFilteredLiveData.setValue(listExpense);
        MainActivity.expenses.clear();
        MainActivity.expenses.addAll(listExpense);

        //cập nhật dữ liệu xuống database
        ContentValues content = new ContentValues();
        content.put("CategoryId", categoryId);
        content.put("ExpenseDate", formatDate);
        content.put("ExpenseTime", time + ":00");
        content.put("ExpenseMoney", money);
        content.put("Note", note);

        String sExpenseId = String.valueOf(expenseId);
        long result = database.update("ChiTieu", content, "ExpenseId = ?", new String[]{sExpenseId});
        return result > 0;
    }

    public void updateFilter(String charString) {
        listExpenseFiltered.clear();
        if (charString.isEmpty()) {
            listExpenseFiltered.addAll(listExpense);
            listExpenseFilteredLiveData.setValue(listExpense);
            return;
        }
        for (ExpenseModel expense : listExpense) {
            String str1 = String.valueOf(expense.categoryName).toLowerCase();
            String str2 = charString.toLowerCase();

            Integer money = tryParse(str2);

            if (str1.contains(str2) ||
                    str1.equals(str2) ||
                    (money != null && money == expense.getExpenseMoney()) ||
                    expense.getNote().toLowerCase().contains(str2)) {
                listExpenseFiltered.add(expense);
            }
        }
        listExpenseFilteredLiveData.setValue(listExpenseFiltered);
    }

    public void cancelFilter() {
        listExpenseFilteredLiveData.setValue(listExpense);
    }

    public Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}