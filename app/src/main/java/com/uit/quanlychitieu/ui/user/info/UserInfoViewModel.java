package com.uit.quanlychitieu.ui.user.info;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.UserModel;

public class UserInfoViewModel extends ViewModel {

    private MutableLiveData<String> userName;

    private MutableLiveData<String> displayName;

    private MutableLiveData<String> email;

    private MutableLiveData<String> dateAdd;

    private MutableLiveData<String> dateModify;

    private UserModel user;

    public UserInfoViewModel() {
        userName = new MutableLiveData<>();
        displayName = new MutableLiveData<>();
        email = new MutableLiveData<>();
        dateAdd = new MutableLiveData<>();
        dateModify = new MutableLiveData<>();
        initData();
    }

    public void initData() {
        for (UserModel user : MainActivity.users) {
            if (MainActivity.USER_ID == user.getUserId()) {
                this.user = user;
                break;
            }
        }
        if (user != null) {
            userName.setValue(user.getUserName());
            displayName.setValue(user.getDisplayName());
            email.setValue(user.getEmail());
            dateAdd.setValue(user.dateAddFormated);
            dateModify.setValue(user.dateModifyFormated);
        }
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<String> getDisplayName() {
        return displayName;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getDateAdd() {
        return dateAdd;
    }

    public LiveData<String> getDateModify() {
        return dateModify;
    }
}