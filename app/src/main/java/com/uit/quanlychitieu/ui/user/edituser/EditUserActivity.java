package com.uit.quanlychitieu.ui.user.edituser;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.uit.quanlychitieu.Language;
import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.databinding.ActivityEditUserBinding;
import com.uit.quanlychitieu.databinding.ActivityLoginBinding;
import com.uit.quanlychitieu.ui.expense.ExpenseViewModel;
import com.uit.quanlychitieu.ui.login.LoginActivity;
import com.uit.quanlychitieu.ui.login.LoginViewModel;
import com.uit.quanlychitieu.ui.login.LoginViewModelFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditUserActivity extends AppCompatActivity implements EditUserCallbacks {

    public static final int RESULT_CODE = 309;
    private EditUserViewModel mViewModel;
    private CircleImageView imgUser;
    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private Button btnOk, btnCancel;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Language.setLanguage(EditUserActivity.this, LoginActivity.LANGUAGE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.edit_user_info_title));
        }

        ActivityEditUserBinding activityEditUserBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_user);
        mViewModel = new ViewModelProvider(this, new EditUserViewModelFactory(this)).get(EditUserViewModel.class);
        activityEditUserBinding.setViewModel(mViewModel);

        imgUser = findViewById(R.id.imgUser);
        displayImageUser();
    }

    private void displayImageUser() {
        intent = getIntent();
        String filePath = intent.getStringExtra("path");
//        String userName = intent.getStringExtra("userName");
//        String displayName = intent.getStringExtra("displayName");
//        String email = intent.getStringExtra("email");

        try {
            byte[] imgArray = getByteArray(filePath);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgArray, 0, imgArray.length);
            imgUser.setImageBitmap(bitmap);
        } catch (Exception ex) {
            Log.e("ERROR", ex.getMessage());
        }
    }

    private byte[] getByteArray(String filePath) throws IOException {
        File file = new File(filePath);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
        buf.read(bytes, 0, bytes.length);
        buf.close();
        return bytes;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ok_cancel, menu);
        return true;
    }

    // Khi người dùng hoàn tất chỉnh sửa thông tin
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel:
                supportFinishAfterTransition();
                break;
            case R.id.ok:
                boolean result = saveUserInfo();
                if (!result) {
                    return super.onOptionsItemSelected(item);
                }
                supportFinishAfterTransition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Lưu ảnh vào file (mảng byte)
    public String saveTempFileImageAsByteArray(Context context, byte[] img, String name) {
        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name);
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            os.write(img);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("ERORR", "Error writing file", e);
        }
        return imageFile.getAbsolutePath();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imgUser.setImageBitmap(selectedImage);
                } catch (Exception ex) {
                    Toast.makeText(this, "Không thể tải hình ảnh!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                return;
        }
    }

    // Thực hiện khi người dùng muốn thay đổi hình ảnh đại diện
    @Override
    public void onChangeImageUser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Đổi ảnh đại điện"), 10);
    }

    // Thực hiện khi người dùng chọn Thay đổi mật khẩu
    @Override
    public void onChangePassword() {
        final Dialog dialog = new Dialog(EditUserActivity.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_change_password);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        edtOldPassword = dialog.findViewById(R.id.edtOldPassword);
        edtNewPassword = dialog.findViewById(R.id.edtNewPassword);
        edtConfirmPassword = dialog.findViewById(R.id.edtConfirmPassword);
        btnOk = dialog.findViewById(R.id.btnOk);
        btnCancel = dialog.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldPassword = String.valueOf(edtOldPassword.getText());
                String newPassword = String.valueOf(edtNewPassword.getText());
                String confirmPassword = String.valueOf(edtConfirmPassword.getText());
                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(EditUserActivity.this, "Bạn chưa điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                int result = mViewModel.changePassword(oldPassword, newPassword, confirmPassword);
                if (result == 0 || result == -1) {
                    return;
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //Thực hiện khi người dùng thay đổi mật khẩu thành công
    @Override
    public void onChangePasswordSuccess(String message) {
        Toast.makeText(EditUserActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    // Thực hiện khi người dùng nhập mật khẩu cũ không chính xác
    @Override
    public void onChangePasswordFailure(String message) {
        Toast.makeText(EditUserActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    // Thực hiện khi 2 mật khẩu mới người dùng nhập không trùng khớp
    @Override
    public void onChangePasswordFailure1(String message) {
        Toast.makeText(EditUserActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    //Lưu lại thay đổi của người dùng
    private boolean saveUserInfo() {
        String newDisplayName = mViewModel.getDisplayName();
        String newEmail = mViewModel.getEmail();

        Bitmap bm = ((BitmapDrawable) imgUser.getDrawable()).getBitmap();
        byte[] newImg = MainActivity.getBitmapAsByteArray(bm);
        String path = saveTempFileImageAsByteArray(EditUserActivity.this, newImg, "imgAvatar");

        if (newDisplayName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(EditUserActivity.this, "Bạn chưa điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }

        Date currentTime = Calendar.getInstance().getTime();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String formated = format.format(currentTime);

        mViewModel.updateUserInfo(newDisplayName, newEmail, newImg, formated);
        Toast.makeText(EditUserActivity.this, "Thông tin đã được cập nhật", Toast.LENGTH_SHORT).show();

        intent.putExtra("newDisplayName", newDisplayName);
        intent.putExtra("newEmail", newEmail);
        intent.putExtra("dateModify", formated);
        intent.putExtra("path", path);
        setResult(RESULT_CODE, intent);
        return true;
    }
}