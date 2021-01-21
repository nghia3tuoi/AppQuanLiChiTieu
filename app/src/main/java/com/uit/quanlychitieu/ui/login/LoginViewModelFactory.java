package com.uit.quanlychitieu.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LoginViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private LoginCallbacks loginCallbacks;

    public LoginViewModelFactory(LoginCallbacks loginCallbacks) {
        this.loginCallbacks = loginCallbacks;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LoginViewModel(loginCallbacks);
    }
}
