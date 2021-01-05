package com.uit.quanlychitieu;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.uit.quanlychitieu.model.UserModel;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnChangeUser, btnBack, btnLogin;
    private EditText edtUserName, edtPassword;
    private UserModel user;

    ImageView imgUserName, imgEmail, imgAddDate, imgModifyDate;
    CircleImageView circleImageView;
    private TextView txtDisplayName, txtUserName, txtEmail, txtAddDate, txtModifyDate;

    //Chạm vào các control này sẽ đóng màn hình
    private LinearLayout layoutSpace;
    private TextView txtSpace;

    @Override
    public void onClick(View v) {
        finishAfterTransition();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        imgUserName = findViewById(R.id.imgUserName);
        imgEmail = findViewById(R.id.imgEmail);
        imgAddDate = findViewById(R.id.imgAddDate);
        imgModifyDate = findViewById(R.id.imgModifyDate);

        layoutSpace = findViewById(R.id.layoutSpace);
        txtSpace = findViewById(R.id.txtSpace);
        layoutSpace.setOnClickListener(this);
        txtSpace.setOnClickListener(this);

        imgUserName.setImageResource(R.drawable.username_detail);
        imgEmail.setImageResource(R.drawable.email_detail);
        imgAddDate.setImageResource(R.drawable.calendar_detail_user);
        imgModifyDate.setImageResource(R.drawable.calendar_clock_detail);

        btnChangeUser = findViewById(R.id.btnChangeUser);
        btnChangeUser.setOnClickListener(clickChangeUser);

        user = (UserModel) getIntent().getSerializableExtra("user");
        circleImageView = findViewById(R.id.imgUser);
        txtDisplayName = findViewById(R.id.txtDisplayName);
        txtUserName = findViewById(R.id.txtUserName);
        txtEmail = findViewById(R.id.txtEmail);
        txtAddDate = findViewById(R.id.txtAddDate);
        txtModifyDate = findViewById(R.id.txtModifyDate);

        displayUserInfo();
    }

    private void displayUserInfo() {
        if (user != null) {
            txtDisplayName.setText(user.getDisplayName().toUpperCase());
            txtUserName.setText(user.getUserName());
            txtEmail.setText(user.getEmail());

            SimpleDateFormat formatDate = new SimpleDateFormat("dd-mm-yyyy");

            String sDateAdd = user.getDateAdd(), sDateModify = user.getDateModify();
            Date dateAdd = new SimpleDateFormat("dd/mm/yyyy").parse(sDateAdd, new ParsePosition(0));
            Date dateModify = new SimpleDateFormat("dd/mm/yyyy").parse(sDateModify, new ParsePosition(0));
            txtAddDate.setText(formatDate.format(dateAdd));
            txtModifyDate.setText(formatDate.format(dateModify));
        }
    }

    private final View.OnClickListener clickChangeUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(UserInfoActivity.this);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_change_user_login);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (dialog != null && dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            edtUserName = dialog.findViewById(R.id.edtUserName);
            edtPassword = dialog.findViewById(R.id.edtPassword);
            btnBack = dialog.findViewById(R.id.btnBack);
            btnLogin = dialog.findViewById(R.id.btnLogin);

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Toast.makeText(UserInfoActivity.this, "Đang đăng nhập...", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.show();
        }
    };
}