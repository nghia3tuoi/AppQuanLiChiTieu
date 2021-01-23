package com.uit.quanlychitieu.ui.statistic.category_statistic;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CategoryStatisticViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private CategoryStatisticCallbacks categotyStatisticCallbacks;

    public CategoryStatisticViewModelFactory(CategoryStatisticCallbacks categotyStatisticCallbacks) {
        this.categotyStatisticCallbacks = categotyStatisticCallbacks;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CategoryStatisticViewModel(categotyStatisticCallbacks);
    }
}
