package com.uit.quanlychitieu.ui.statistic.category_statistic;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.model.IncomeModel;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CategoryStatisticViewModel extends ViewModel {

    private List<CategoryModel> categoriesExpense, categoriesIncome;
    private SQLiteDatabase database;
    int USER_ID;
    private CategoryStatisticCallbacks categotyStatisticCallbacks;
    private String startDate, endDate;
    private String type;

    public CategoryStatisticViewModel(CategoryStatisticCallbacks categotyStatisticCallbacks) {
        database = MainActivity.database;
        USER_ID = MainActivity.USER_ID;
        categoriesExpense = new ArrayList<>(MainActivity.categoryExpanses);
        categoriesIncome = new ArrayList<>(MainActivity.categoryIncomes);
        this.categotyStatisticCallbacks = categotyStatisticCallbacks;
        initData();
    }

    private void initData() {
        // lấy dữ liệu ngày tháng hiện tại
        Date dEndDate = Calendar.getInstance().getTime(); //Ngày kết thúc (ngày hiện tại)
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String formated = format.format(dEndDate);
        endDate = formated;

        //lấy ngày tháng hiện tại trừ đi 1 tháng
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dEndDate);
        calendar.add(Calendar.MONTH, -1);
        Date dStartDate = calendar.getTime(); //Ngày bắt đầu (cách ngày kết thúc 1 tháng)
        String formated1 = format.format(dStartDate);
        startDate = formated1;
        type = "ChiTieu";

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        DataPieChart data = (type == "ChiTieu") ? getDataExpense() : getDataIncome();
        if (data != null) {
            categotyStatisticCallbacks.onDataChanged(data.getCategoryName(), data.getValues());
        }
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
        DataPieChart data = (type == "ChiTieu") ? getDataExpense() : getDataIncome();
        if (data != null) {
            categotyStatisticCallbacks.onDataChanged(data.getCategoryName(), data.getValues());
        }
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
        DataPieChart data = (type == "ChiTieu") ? getDataExpense() : getDataIncome();
        if (data != null) {
            categotyStatisticCallbacks.onDataChanged(data.getCategoryName(), data.getValues());
        }
    }

    public void onClickSelectStartDate(View view) {

        categotyStatisticCallbacks.onSelectStartDate();
    }

    public void onClickSelectEndDate(View view) {

        categotyStatisticCallbacks.onSelectEndDate();
    }

    public DataPieChart getDataExpense() {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = new SimpleDateFormat("dd-MM-yyyy").parse(startDate, new ParsePosition(0));
        String from = format.format(d1);
        Date d2 = new SimpleDateFormat("dd-MM-yyyy").parse(endDate, new ParsePosition(0));
        String to = format.format(d2);

        List<String> categoryName = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        Cursor cursor = database.rawQuery("select CategoryId, sum(ExpenseMoney) as Sum from ChiTieu where UserId =  " + USER_ID + " and (ExpenseDate between '" + from + "' and '" + to + "') group by CategoryId", null);

        try {
            int i = 0;
            while (cursor.moveToNext()) {
                int categoryId = cursor.getInt(0);
                for (CategoryModel category : categoriesExpense) {
                    if (categoryId == category.getCategoryId()) {
                        categoryName.add(category.getName());
                        break;
                    }
                }
                String sMoney = cursor.getString(1);
                Integer money = new Integer(sMoney);
                values.add(money);
                i++;
            }
        } catch (Exception ex) {
            return new DataPieChart(null, null);
        }
        cursor.close();
        DataPieChart data = new DataPieChart(categoryName, values);
        return data;
    }

    public DataPieChart getDataIncome() {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = new SimpleDateFormat("dd-MM-yyyy").parse(startDate, new ParsePosition(0));
        String from = format.format(d1);
        Date d2 = new SimpleDateFormat("dd-MM-yyyy").parse(endDate, new ParsePosition(0));
        String to = format.format(d2);

        List<String> categoryName = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        Cursor cursor = database.rawQuery("select CategoryId, sum(IncomeMoney)as Sum from ThuNhap where UserId =  " + USER_ID + " and (IncomeDate between '" + from + "' and '" + to + "') group by CategoryId", null);

        try {
            int i = 0;
            while (cursor.moveToNext()) {
                int categoryId = cursor.getInt(0);
                for (CategoryModel category : categoriesIncome) {
                    if (categoryId == category.getCategoryId()) {
                        categoryName.add(category.getName());
                        break;
                    }
                }
                String sMoney = cursor.getString(1);
                Integer money = new Integer(sMoney);
                values.add(money);
                i++;
            }
        } catch (Exception ex) {
            return new DataPieChart(null, null);
        }
        cursor.close();
        DataPieChart data = new DataPieChart(categoryName, values);
        return data;
    }
}

class DataPieChart {
    private List<String> categoryName;
    private List<Integer> values;

    public DataPieChart(List<String> categoryName, List<Integer> values) {
        this.categoryName = categoryName;
        this.values = values;
    }

    public String[] getCategoryName() {

        String[] array = new String[categoryName.size()];
        categoryName.toArray(array);
        return array;
    }

    public Integer[] getValues() {
        Integer[] array = new Integer[values.size()];
        values.toArray(array);
        return array;
    }
}
