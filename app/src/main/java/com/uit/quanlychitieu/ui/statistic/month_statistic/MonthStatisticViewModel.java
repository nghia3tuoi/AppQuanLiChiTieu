package com.uit.quanlychitieu.ui.statistic.month_statistic;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.IncomeModel;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MonthStatisticViewModel extends ViewModel {

    private SQLiteDatabase database;
    private int USER_ID;
    private MonthStatisticCallbacks monthStatisticCallbacks;
    private String numberOfMonth;
    private List<Integer> years;
    private int positionSelected = 0;

    public MonthStatisticViewModel(MonthStatisticCallbacks monthStatisticCallbacks) {
        database = MainActivity.database;
        USER_ID = MainActivity.USER_ID;
        this.monthStatisticCallbacks = monthStatisticCallbacks;
        initData();
    }

    private void initData() {
        years = loadYearFromDatabase();
        numberOfMonth = "12 tháng";
        dataChanged();
    }

    public int getPositionSelected() {
        return positionSelected;
    }

    public void setPositionSelected(int positionSelected) {
        this.positionSelected = positionSelected;
        dataChanged();
    }

    private void dataChanged() {
        DataBarChart data = loadDataBarChar();
        if (data != null) {
            monthStatisticCallbacks.onDataChanged(data.getMoneyExpense(), data.getMoneyIncome());
        }
    }

    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }

    public String getNumberOfMonth() {
        return numberOfMonth;
    }

    public void setNumberOfMonth(String numberOfMonth) {
        this.numberOfMonth = numberOfMonth;
    }

    private List<Integer> loadYearFromDatabase() {
        List<Integer> years = new ArrayList<>();
        Cursor cursor = database.rawQuery("select distinct strftime('%Y', ExpenseDate) as Year from ChiTieu where UserId = " + USER_ID, null);
        while (cursor.moveToNext()) {
            String sYear = cursor.getString(0);
            Integer year = new Integer(sYear);
            years.add(year);
        }
        cursor.close();

        Cursor cursor1 = database.rawQuery("select distinct strftime('%Y', IncomeDate) as Year from ThuNhap where UserId = " + USER_ID, null);
        while (cursor.moveToNext()) {
            String sYear1 = cursor.getString(0);
            int year1 = new Integer(sYear1);
            if (!years.contains(year1)) {
                years.add(year1);
            }
        }
        cursor1.close();
        Collections.sort(years, Collections.reverseOrder());
        return years;
    }

    public DataBarChart loadDataBarChar() {
        List<Integer> expenses = loadData(true);
        List<Integer> incomes = loadData(false);

        DataBarChart data = new DataBarChart(expenses, incomes);
        return data;
    }

    private List<Integer> loadData(boolean isExpense) {

        int yearSelected = years.get(positionSelected);
        String query = isExpense == true ? "select strftime('%m', ExpenseDate ) as Month , sum(ExpenseMoney) as Money from ChiTieu where strftime('%Y', ExpenseDate) = '" + yearSelected + "' and UserId = " + USER_ID + " group by strftime('%m', ExpenseDate)"
                : "select strftime('%m', IncomeDate ) as Month , sum(IncomeMoney) as Money from ThuNhap where strftime('%Y', IncomeDate ) = '" + yearSelected + "' and UserId = " + USER_ID + " group by strftime('%m', IncomeDate)";

        Cursor cursor = database.rawQuery(query, null);

        List<Integer> values = new ArrayList<>();
        int monthCurrent = 1;
        while (cursor.moveToNext()) {
            try {

                String sMonth = cursor.getString(0);
                String sMoney = cursor.getString(1);

                Integer month = new Integer(sMonth);
                while (monthCurrent != month) {
                    values.add(0);
                    monthCurrent++;
                }

                Integer value = new Integer(sMoney);
                values.add(value);
                monthCurrent++;

            } catch (Exception ex) {
                return new ArrayList<>();
            }
        }
        while (values.size() < 12) {
            values.add(0);
        }
        return values;
    }

    public class DataBarChart {
        private List<Integer> moneyExpense;
        private List<Integer> moneyIncome;

        public DataBarChart(List<Integer> moneyExpense, List<Integer> moneyIncome) {
            this.moneyExpense = moneyExpense;
            this.moneyIncome = moneyIncome;
        }

        public Integer[] getMoneyExpense() {
            Integer[] array = new Integer[moneyExpense.size()];
            moneyExpense.toArray(array);
            return array;
        }

        public void setMoneyExpense(List<Integer> moneyExpense) {
            this.moneyExpense = moneyExpense;
        }

        public Integer[] getMoneyIncome() {
            Integer[] array = new Integer[moneyIncome.size()];
            moneyIncome.toArray(array);
            return array;
        }

        public void setMoneyIncome(List<Integer> moneyIncome) {
            this.moneyIncome = moneyIncome;
        }
    }
}
