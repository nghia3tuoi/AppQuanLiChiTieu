package com.uit.quanlychitieu.ui.statistic.week_statistic;

import android.view.View;

import androidx.lifecycle.ViewModel;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;

public class WeekStatisticViewModel extends ViewModel {
    private String startDate, endDate;
    private WeekStatisticCallbacks weekStatisticCallbacks;

    public WeekStatisticViewModel(WeekStatisticCallbacks weekStatisticCallbacks) {
        this.weekStatisticCallbacks = weekStatisticCallbacks;
        initData();
    }

    private void initData() {

        // lấy dữ liệu ngày tháng hiện tại
        Date current = Calendar.getInstance().getTime();

        //lấy ngày tháng hiện tại trừ đi 7 ngày
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.DATE, -7);
        Date dStartDate = calendar.getTime(); //Ngày bắt đầu (cách ngày hiện tại 7 ngày)
        setStartDate(new SimpleDateFormat("dd-MM-yyyy").format(dStartDate));
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;

        dataChanged();
    }

    public void dataChanged() {
        Date d = new SimpleDateFormat("dd-MM-yyyy").parse(startDate, new ParsePosition(0));

        //lấy ngày tháng hiện tại cộng thêm 7 ngày
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.DATE, 7);
        Date dStartDate = calendar.getTime(); //Ngày bắt đầu (cách ngày hiện tại 7 ngày)
        endDate = new SimpleDateFormat("dd-MM-yyyy").format(dStartDate);

        WeekStatisticModel weekStatisticModel = new WeekStatisticModel(this.startDate, this.endDate);
        WeekStatisticModel.DataLineChart data = weekStatisticModel.getData();

        if (data != null) {
            weekStatisticCallbacks.onDataChanged(data.getDateOfWeek(), data.getMoneyExpense(), data.getMoneyIncome());
        }
    }

    public void onClickSelectStartDate(View view) {

        weekStatisticCallbacks.onSelectStartDate();
    }

}
