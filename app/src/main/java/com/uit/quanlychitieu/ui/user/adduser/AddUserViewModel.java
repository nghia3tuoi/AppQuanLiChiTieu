package com.uit.quanlychitieu.ui.user.adduser;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.model.UserModel;
import com.uit.quanlychitieu.ui.login.LoginActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddUserViewModel extends ViewModel {

    private SQLiteDatabase database;
    private int USER_ID;
    private List<UserModel> users;

    private String displayName, email, username, password, confirmPassword;

    private AddUserCallbacks addUserCallbacks;

    public AddUserViewModel(AddUserCallbacks addUserCallbacks) {
        this.addUserCallbacks = addUserCallbacks;
        database = LoginActivity.database;
        initData();
        users = new ArrayList<>(LoginActivity.users);
    }

    @BindingAdapter("bind:imageBitmap")
    public static void loadImage(ImageView iv, Bitmap bitmap) {
        iv.setImageBitmap(bitmap);
    }

    private void initData() {
        displayName = email = username = password = confirmPassword = "";
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    private int checkDataIsValid() {
        // thông tin chưa được điền đầy đủ
        if (displayName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return 0;
        }
        // 2 mật khẩu không giống nhau
        if (!password.equals(confirmPassword)) {
            return -1;
        }
        // email không hợp lệ
        if (!email.contains("@")) {
            return -2;
        }
        // tên người dùng hiện đang tồn tại
        for (UserModel user : users) {
            if (username.equals(user.getUserName())) {
                return -3;
            }
        }
        return 1;
    }

    public void onClickAddUser(View view) {

        int resultCheck = checkDataIsValid();
        switch (resultCheck) {
            case -3:
                addUserCallbacks.onFailure("Người dùng này đã tồn tại, vui lòng chọn tên đăng nhập khác");
                break;
            case -2:
                addUserCallbacks.onFailure("Email không hợp lệ!");
                break;
            case -1:
                addUserCallbacks.onFailure("Mật khẩu của bạn không trùng khớp!");
                break;
            case 0:
                addUserCallbacks.onFailure("Bạn chưa điền đầy đủ thông tin");
                break;
            case 1:
                boolean resultAddUser = addUser();
                if (resultAddUser) {
                    addUserCallbacks.onSuccess("Thêm người dùng thành công");
                } else {
                    addUserCallbacks.onFailure("Không thể thêm người dùng ngay lúc này!");
                }
                break;
        }
    }

    private boolean addUser() {

        // lấy dữ liệu ngày tháng hiện tại
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String formated = format.format(currentTime);

        byte[] imgUser = AddUserActivity.bitmapByteArray;
        //thêm dữ liệu vào database
        ContentValues content = new ContentValues();
        content.put("DisplayName", displayName);
        content.put("UserName", username);
        content.put("Password", password);
        content.put("Email", email);
        content.put("DateAdd", formated);
        content.put("DateModify", formated);
        content.put("ImageAvatar", imgUser);

        //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.username_detail);

        long result = database.insert("User", null, content);
        if (result > 0) {

            int userId = LoginActivity.users.get(users.size() - 1).getUserId() + 1;
            UserModel user = new UserModel(userId, displayName, username, password, email, imgUser, formated, formated);
            LoginActivity.users.add(user);
        }
        return result > 0;
    }

    public void onClickImageUser(View view) {
        addUserCallbacks.onChangeImageUser();
    }
}
