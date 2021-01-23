package com.uit.quanlychitieu.ui.statistic.week_statistic;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class WeekStatisticViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private WeekStatisticCallbacks weekStatisticCallbacks;

    public WeekStatisticViewModelFactory(WeekStatisticCallbacks weekStatisticCallbacks) {
        this.weekStatisticCallbacks = weekStatisticCallbacks;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new WeekStatisticViewModel(weekStatisticCallbacks);
    }
}
