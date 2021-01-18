package com.uit.quanlychitieu.ui.expense;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.model.ExpenseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class ExpenseViewModel extends ViewModel {

    MutableLiveData<List<ExpenseModel>> listExpenseLiveData;
    List<ExpenseModel> listExpense;

    MutableLiveData<List<ExpenseModel>> listExpenseFilteredLiveData;
    List<ExpenseModel> listExpenseFiltered;

    public ExpenseViewModel() {
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

    public void addExpense(int categoryId, String date, String time, int money, String note) {
        int expenseid = listExpense.get(listExpense.size() - 1).getExpenseId() + 1;
        ExpenseModel expense = new ExpenseModel(expenseid, categoryId, date, time + ":00", money, note, MainActivity.USER_ID);
        listExpense.add(expense);
        listExpenseLiveData.setValue(listExpense);
        listExpenseFilteredLiveData.setValue(listExpense);
        MainActivity.expenses.add(expense);
        //thêm dữ liệu vào database
    }

    public void deleteExpense(int position) {
        ExpenseModel expense = listExpense.get(position);
        listExpense.remove(position);
        listExpenseLiveData.setValue(listExpense);
        listExpenseFilteredLiveData.setValue(listExpense);
        MainActivity.expenses.remove(position);
        //xóa dữ liệu trong database
    }

    public void updateExpense(int expenseId, int categoryId, String date, String time, int money, String note) {
        for (ExpenseModel expense : listExpense) {
            if (expense.getExpenseId() == expenseId && expense.getUserId() == MainActivity.USER_ID) {
                expense.setCategoryId(categoryId);
                expense.setExpenseDate(date);
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