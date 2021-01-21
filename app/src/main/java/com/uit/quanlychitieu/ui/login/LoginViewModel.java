package com.uit.quanlychitieu.ui.login;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
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

    private LoginModel loginModel;
    private LoginCallbacks loginCallbacks;

    public LoginViewModel(LoginCallbacks loginCallbacks) {
        this.loginCallbacks = loginCallbacks;
        loginModel = new LoginModel();
    }

    public LoginModel getLoginModel() {
        return loginModel;
    }

    public void setLoginModel(LoginModel loginModel) {
        this.loginModel = loginModel;
    }

    public LoginCallbacks getLoginCallbacks() {
        return loginCallbacks;
    }

    public void setLoginCallbacks(LoginCallbacks loginCallbacks) {
        this.loginCallbacks = loginCallbacks;
    }

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void onClickButtonLogin(View view) {

        loginModel = new LoginModel(username, password);
        int result = loginModel.checkUserInfo();
        LoginActivity.USER_ID_LOGIN = result;
        if (result == -1) {
            loginCallbacks.onFailure("Tên người dùng hoặc mật khẩu không đúng!");
        } else {
            loginCallbacks.onSuccess("Đăng nhập thành công");
        }
    }

    public void onClickRegister(View view) {
        loginCallbacks.onRegister();
    }
}
