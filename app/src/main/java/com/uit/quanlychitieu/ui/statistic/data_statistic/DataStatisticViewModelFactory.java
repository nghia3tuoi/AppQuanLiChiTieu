package com.uit.quanlychitieu.ui.statistic.data_statistic;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.uit.quanlychitieu.ui.login.LoginCallbacks;
import com.uit.quanlychitieu.ui.login.LoginViewModel;

public class DataStatisticViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private DataStatisticCallbacks dataStatisticCallbacks;

    public DataStatisticViewModelFactory(DataStatisticCallbacks dataStatisticCallbacks) {
        this.dataStatisticCallbacks = dataStatisticCallbacks;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DataStatisticViewModel(dataStatisticCallbacks);
    }
}
