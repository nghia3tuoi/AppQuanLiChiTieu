package com.uit.quanlychitieu.ui.userinfo;

import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.UserModel;

import java.util.ArrayList;

public class UserInfoViewModel extends ViewModel {

    SQLiteDatabase database;

    public UserInfoViewModel() {

        database = MainActivity.database;
    }

    public boolean isValidPassword(String username, String password) {
        for (UserModel user : MainActivity.users) {
            if (user.getUserName().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
}
