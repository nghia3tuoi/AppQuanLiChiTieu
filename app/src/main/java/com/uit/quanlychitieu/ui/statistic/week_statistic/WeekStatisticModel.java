package com.uit.quanlychitieu.ui.statistic.week_statistic;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.uit.quanlychitieu.MainActivity;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

public class WeekStatisticModel {

    private int USER_ID;
    private SQLiteDatabase database;
    private String fromDate, toDate;

    public WeekStatisticModel(String fromDate, String toDate) {
        database = MainActivity.database;
        USER_ID = MainActivity.USER_ID;
        Date d1 = new SimpleDateFormat("dd-MM-yyyy").parse(fromDate, new ParsePosition(0));
        Date d2 = new SimpleDateFormat("dd-MM-yyyy").parse(toDate, new ParsePosition(0));
        this.fromDate = new SimpleDateFormat("yyyy-MM-dd").format(d1);
        this.toDate = new SimpleDateFormat("yyyy-MM-dd").format(d2);
    }

    public DataLineChart getData() {
        List<Integer> expenses = getMoney(true);
        List<Integer> incomes = getMoney(false);

        Date d = new SimpleDateFormat("yyyy-MM-dd").parse(fromDate, new ParsePosition(0));
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        return new DataLineChart(dayOfWeek, expenses, incomes);
    }

    private List<Integer> getMoney(boolean isExpense) {
        List<Integer> values = new ArrayList<>();
        String query = isExpense == true ? "select ExpenseDate, sum(ExpenseMoney) as Sum from ChiTieu where UserId = " + USER_ID +
                " and ExpenseDate >= '" + fromDate + "' and ExpenseDate <= '" + toDate + "' group by ExpenseDate order by ExpenseDate"
                : "select IncomeDate, sum(IncomeMoney) as Sum from ThuNhap where UserId = " + USER_ID +
                " and IncomeDate >= '" + fromDate + "' and IncomeDate <= '" + toDate + "' group by IncomeDate order by IncomeDate";
        Cursor cursor = database.rawQuery(query, null);
        Date temp = new SimpleDateFormat("yyyy-MM-dd").parse(fromDate, new ParsePosition(0));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(temp);
        calendar.add(Calendar.DATE, -1);
        Date dPreviousDate = calendar.getTime();

        String sDateCurrent = new SimpleDateFormat("yyyy-MM-dd").format(dPreviousDate);

        while (cursor.moveToNext()) {
            try {

                Date d = new SimpleDateFormat("yyyy-MM-dd").parse(sDateCurrent);
                //Calendar calendar = Calendar.getInstance();
                calendar.setTime(d);
                calendar.add(Calendar.DATE, 1);
                Date dNextDate = calendar.getTime();
                sDateCurrent = new SimpleDateFormat("yyyy-MM-dd").format(dNextDate);

                String sDate = cursor.getString(0);
                String sTotalMoney = cursor.getString(1);

                while (!sDateCurrent.equals(sDate)) {
                    values.add(0);

                    Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(sDateCurrent);
                    //Calendar calendar = Calendar.getInstance();
                    calendar.setTime(d1);
                    calendar.add(Calendar.DATE, 1);
                    Date dNextDate1 = calendar.getTime();
                    sDateCurrent = new SimpleDateFormat("yyyy-MM-dd").format(dNextDate1);
                }

                Integer value = new Integer(sTotalMoney);
                values.add(value);

            } catch (Exception ex) {
                return new ArrayList<>();
            }
        }

        while (values.size() < 7) {
            values.add(0);
        }

        cursor.close();
        return values;
    }

    public class DataLineChart {

        private int dateOfWeek;
        private List<Integer> moneyExpense;
        private List<Integer> moneyIncome;

        public DataLineChart(int dateOfWeek, List<Integer> moneyExpense, List<Integer> moneyIncome) {
            this.dateOfWeek = dateOfWeek;
            this.moneyExpense = moneyExpense;
            this.moneyIncome = moneyIncome;
        }

        public int getDateOfWeek() {
            return dateOfWeek;
        }

        public void setDateOfWeek(int dateOfWeek) {
            this.dateOfWeek = dateOfWeek;
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
