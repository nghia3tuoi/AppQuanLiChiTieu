package com.uit.quanlychitieu.ui.user;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    // TODO: Implement the ViewModel
    private MutableLiveData<String> mText;

    public UserViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery User");
    }

    public LiveData<String> getText() {
        return mText;
    }
}