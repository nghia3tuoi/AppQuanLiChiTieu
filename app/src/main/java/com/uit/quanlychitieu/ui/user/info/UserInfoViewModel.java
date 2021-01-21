package com.uit.quanlychitieu.ui.user.info;

import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.UserModel;

public class UserInfoViewModel extends ViewModel {

    MutableLiveData<String> userName;
    MutableLiveData<String> displayName;
    MutableLiveData<String> email;
    MutableLiveData<String> dateAdd;
    MutableLiveData<String> dateModify;

    UserModel user;
    SQLiteDatabase database;
    int USER_ID;

    public UserInfoViewModel() {
        database = MainActivity.database;
        USER_ID = MainActivity.USER_ID;
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

    public void deleteUser() {
        //xóa dữ liệu trong database
        String sUserId = String.valueOf(USER_ID);
        database.delete("ChiTieu", "UserId = ?", new String[]{String.valueOf(sUserId)});
        database.delete("ThuNhap", "UserId = ?", new String[]{String.valueOf(sUserId)});
        database.delete("User", "UserId = ?", new String[]{String.valueOf(sUserId)});
    }
}