package com.uit.quanlychitieu.ui.login;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class LoginModel {

    private List<UserModel> listUser;

    @Nullable
    private String username, password;

    public LoginModel() {
        listUser = new ArrayList<>(LoginActivity.users);
    }

    public LoginModel(@Nullable String username, @Nullable String password) {
        this.username = username;
        this.password = password;
        listUser = new ArrayList<>(LoginActivity.users);
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }

    //Kiểm tra thông tin người dùng và trả về USER_ID (Không tìm thấy người dùng USER_ID = -1)
    public int checkUserInfo() {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return -1;
        }
        for (UserModel user : listUser) {
            if (username.equals(user.getUserName()) && password.equals(user.getPassword())) {
                return user.getUserId();
            }
        }
        return -1;
    }

}
