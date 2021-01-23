package com.uit.quanlychitieu.ui.statistic.data_statistic;

import android.app.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;

public interface DataStatisticCallbacks {

    public void onSelectStartDate();

    public void onSelectEndDate();

    public void onDisplayResult(String totalExpense, String totalIncome, String balance);
}
