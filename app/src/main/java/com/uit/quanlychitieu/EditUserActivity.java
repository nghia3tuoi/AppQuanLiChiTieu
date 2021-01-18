package com.uit.quanlychitieu;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.uit.quanlychitieu.model.UserModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditUserActivity extends AppCompatActivity {

    private CircleImageView imgUser;
    private Button changePassword;

    private EditText edtUserName, edtDisplayName, edtEmail;
    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;

    private Button btnOk, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        imgUser = findViewById(R.id.imgUser);
        edtUserName = findViewById(R.id.edtUserName);
        edtDisplayName = findViewById(R.id.edtDisplayName);
        edtEmail = findViewById(R.id.edtEmail);

        Intent intent = getIntent();
        String filePath = intent.getStringExtra("path");
        String userName = intent.getStringExtra("userName");
        String displayName = intent.getStringExtra("displayName");
        String email = intent.getStringExtra("email");
        displayTextFormated(filePath, userName, displayName, email);

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Đổi ảnh đại điện"), 10);
            }
        });

        changePassword = findViewById(R.id.btnChangePassword);
        changePassword.setOnClickListener(clickChangePasswordShowDialog);
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

    private void displayTextFormated(String filePath, String username, String displayname, String email) {
        try {
            byte[] bytes = getByteArray(filePath);
            if (bytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgUser.setImageBitmap(bitmap);
            } else {
                imgUser.setImageResource(R.drawable.username_detail);
            }

        } catch (Exception ex) {
            Log.e("ERORR", ex.getMessage());
        }

        edtUserName.setText(username);
        edtDisplayName.setText(displayname);
        edtEmail.setText(email);
    }

    private final View.OnClickListener clickChangePasswordShowDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ok_cancel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel:
                supportFinishAfterTransition();
                break;
            case R.id.ok:
                //code
                supportFinishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
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
}