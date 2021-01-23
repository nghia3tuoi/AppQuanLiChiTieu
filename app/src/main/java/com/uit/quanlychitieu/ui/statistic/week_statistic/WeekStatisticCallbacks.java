package com.uit.quanlychitieu.ui.statistic.week_statistic;

import java.util.ArrayList;
import java.util.List;

public interface WeekStatisticCallbacks {

    public void onSelectStartDate();

    public void onDataChanged(int dayOfWeek, Integer[] expenses, Integer[] incomes);
}
