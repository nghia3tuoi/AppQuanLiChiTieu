package com.uit.quanlychitieu.ui.statistic.month_statistic;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.uit.quanlychitieu.ui.statistic.week_statistic.WeekStatisticCallbacks;
import com.uit.quanlychitieu.ui.statistic.week_statistic.WeekStatisticViewModel;

public class MonthStatisticViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private MonthStatisticCallbacks monthStatisticCallbacks;

    public MonthStatisticViewModelFactory(MonthStatisticCallbacks monthStatisticCallbacks) {
        this.monthStatisticCallbacks = monthStatisticCallbacks;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MonthStatisticViewModel(monthStatisticCallbacks);
    }
}
