package com.uit.quanlychitieu.ui.user.edituser;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.UserModel;
import com.uit.quanlychitieu.ui.login.LoginCallbacks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditUserViewModel extends ViewModel {

    private SQLiteDatabase database;
    private int USER_ID;
    private UserModel user;

    private EditUserCallbacks editUserCallbacks;

    private String userName;
    private String displayName;
    private String email;

    public EditUserViewModel(EditUserCallbacks editUserCallbacks) {
        this.editUserCallbacks = editUserCallbacks;
        database = MainActivity.database;
        USER_ID = MainActivity.USER_ID;
        initData();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private void initData() {
        database = MainActivity.database;
        USER_ID = MainActivity.USER_ID;

        for (UserModel user : MainActivity.users) {
            if (user.getUserId() == USER_ID) {
                this.user = user;
                break;
            }
        }
        // hiển thị thông tin người dùng
        if (user != null) {
            userName = user.getUserName();
            displayName = user.getDisplayName();
            email = user.getEmail();
        }
    }

    //cập nhật dữ liệu xuống database
    public boolean updateUserInfo(String displayName, String email, byte[] img, String dateModify) {

        ContentValues content = new ContentValues();
        content.put("DisplayName", displayName);
        content.put("Email", email);
        content.put("DateModify", dateModify);
        content.put("ImageAvatar", img);

        String sUserId = String.valueOf(USER_ID);
        long result = database.update("User", content, "UserId = ?", new String[]{sUserId});
        return result > 0;
    }

    public int changePassword(String oldPassword, String newPassword, String confirmPassword) {

        if (!oldPassword.equals(user.getPassword())) {
            // mật khẩu cũ không đúng
            editUserCallbacks.onChangePasswordFailure("Mật khẩu cũ của bạn không đúng");
            return -1;
        }

        if (!newPassword.equals(confirmPassword)) {
            // 2 mật khẩu không giống nhau
            editUserCallbacks.onChangePasswordFailure("Mật khẩu mới không trùng khớp");
            return 0;
        }

        //cập nhật dữ liệu xuống database
        ContentValues content = new ContentValues();
        content.put("Password", newPassword);

        String sUserId = String.valueOf(USER_ID);
        long result = database.update("User", content, "UserId = ?", new String[]{sUserId});
        editUserCallbacks.onChangePasswordSuccess("Mật khẩu của bạn đã được thay đổi");
        return 1;
    }

    public void onClickBtnChangePassword(View view) {
        editUserCallbacks.onChangePassword();
    }

    public void onClickImageUser(View view) {
        editUserCallbacks.onChangeImageUser();
    }
}
