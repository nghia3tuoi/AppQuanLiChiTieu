package com.uit.quanlychitieu.ui.statistic.data_statistic;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;

import androidx.lifecycle.ViewModel;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;

public class DataStatisticViewModel extends ViewModel {

    private DataStatisticCallbacks dataStatisticCallbacks;
    private String startDate, endDate;
    private String totalExpense, totalIncome, balance;

    public DataStatisticViewModel(DataStatisticCallbacks dataStatisticCallbacks) {
        this.dataStatisticCallbacks = dataStatisticCallbacks;
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

        caculate();
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(String totalExpense) {
        this.totalExpense = totalExpense;
    }

    public String getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(String totalIncome) {
        this.totalIncome = totalIncome;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public void onClickSelectStartDate(View view) {

        dataStatisticCallbacks.onSelectStartDate();
    }

    public void onClickSelectEndDate(View view) {

        dataStatisticCallbacks.onSelectEndDate();
    }

    public void onClickResult(View view) {

        caculate();
        dataStatisticCallbacks.onDisplayResult(totalExpense, totalIncome, balance);
    }

    private void caculate() {
        DataStatisticModel data = new DataStatisticModel(startDate, endDate);
        long moneyExpense = data.getSumMoneyExpense();
        totalExpense = "- " + NumberFormat.getCurrencyInstance().format(moneyExpense);
        long moneyIncome = data.getSumMoneyIncome();
        totalIncome = "+ " + NumberFormat.getCurrencyInstance().format(moneyIncome);
        long lBalance = Math.abs(moneyExpense - moneyIncome);
        balance = ((moneyExpense > moneyIncome) ? "- " : "+ ") + NumberFormat.getCurrencyInstance().format(lBalance);
    }
}
