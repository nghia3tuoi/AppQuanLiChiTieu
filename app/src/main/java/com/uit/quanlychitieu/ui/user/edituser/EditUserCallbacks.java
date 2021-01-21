package com.uit.quanlychitieu.ui.user.edituser;

public interface EditUserCallbacks {

    public void onChangeImageUser();

    public void onChangePassword();

    public void onChangePasswordSuccess(String message);

    // do mật khẩu cũ không đúng
    public void onChangePasswordFailure(String message);

    // do 2 mật khẩu mới không trùng khớp
    public void onChangePasswordFailure1(String message);
}
