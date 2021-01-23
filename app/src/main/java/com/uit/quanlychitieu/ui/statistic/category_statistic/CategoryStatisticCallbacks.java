package com.uit.quanlychitieu.ui.statistic.category_statistic;

public interface CategoryStatisticCallbacks {

    public void onSelectStartDate();

    public void onSelectEndDate();

    public void onDataChanged(String[] parties, Integer[] values);
}
