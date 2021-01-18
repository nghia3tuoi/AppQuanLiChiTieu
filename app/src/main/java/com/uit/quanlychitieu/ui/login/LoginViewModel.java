package com.uit.quanlychitieu.ui.login;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.ExpenseModel;
import com.uit.quanlychitieu.model.UserModel;
import com.uit.quanlychitieu.ui.user.listuser.ListUserViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class LoginViewModel extends ViewModel {

    private List<UserModel> listUser;

    public LoginViewModel() {
        initData();
    }

    private void initData() {
        listUser = new ArrayList<>(LoginActivity.users);
    }

    public int isValid(String userName, String password) {
        if (userName == null || password == null || userName.isEmpty() || password.isEmpty()) {
            return -1;
        }
        for (UserModel user : listUser) {
            if (userName.equals(user.getUserName()) && password.equals(user.getPassword())) {
                return user.getUserId();
            }
        }
        return -1;
    }
}
