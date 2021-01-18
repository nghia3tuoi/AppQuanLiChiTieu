package com.uit.quanlychitieu.ui.income;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.ExpenseModel;
import com.uit.quanlychitieu.model.IncomeModel;

import java.util.ArrayList;
import java.util.List;

public class IncomeViewModel extends ViewModel {

    MutableLiveData<List<IncomeModel>> listIncomeLiveData;
    List<IncomeModel> listIncome;

    MutableLiveData<List<IncomeModel>> listIncomeFilteredLiveData;
    List<IncomeModel> listIncomeFiltered;

    public IncomeViewModel() {
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

    public void addIncome(int categoryId, String date, String time, int money, String note) {
        int expenseid = listIncome.get(listIncome.size() - 1).getIncomeId() + 1;
        IncomeModel income = new IncomeModel(expenseid, categoryId, date, time + ":00", money, note, MainActivity.USER_ID);
        listIncome.add(income);
        listIncomeLiveData.setValue(listIncome);
        listIncomeFilteredLiveData.setValue(listIncome);
        //thêm dữ liệu vào database
    }

    public void deleteIncome(int position) {
        IncomeModel income = listIncome.get(position);
        listIncome.remove(position);
        listIncomeLiveData.setValue(listIncome);
        listIncomeFilteredLiveData.setValue(listIncome);
        //xóa dữ liệu trong database
    }

    public void updateIncome(int expenseId, int categoryId, String date, String time, int money, String note) {
        for (IncomeModel income : listIncome) {
            if (income.getIncomeId() == expenseId && income.getUserId() == MainActivity.USER_ID) {
                income.setCategoryId(categoryId);
                income.setIncomeDate(date);
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