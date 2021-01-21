package com.uit.quanlychitieu.ui.user.adduser;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AddUserViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private AddUserCallbacks addUserCallbacks;

    public AddUserViewModelFactory(AddUserCallbacks addUserCallbacks) {
        this.addUserCallbacks = addUserCallbacks;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddUserViewModel(addUserCallbacks);
    }
}
