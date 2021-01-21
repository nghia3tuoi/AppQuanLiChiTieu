package com.uit.quanlychitieu.ui.user.edituser;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class EditUserViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private EditUserCallbacks editUserCallbacks;

    public EditUserViewModelFactory(EditUserCallbacks editUserCallbacks) {
        this.editUserCallbacks = editUserCallbacks;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EditUserViewModel(editUserCallbacks);
    }
}
