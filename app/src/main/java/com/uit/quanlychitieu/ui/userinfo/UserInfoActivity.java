package com.uit.quanlychitieu.ui.userinfo;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.uit.quanlychitieu.Language;
import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.model.UserModel;
import com.uit.quanlychitieu.ui.login.LoginActivity;
import com.uit.quanlychitieu.ui.user.UserViewModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private UserInfoViewModel mViewModel;
    private Button btnChangeUser, btnBack, btnLogin;
    private EditText edtUserName, edtPassword;
    private int USER_ID = -1;
    private ProgressDialog mProgress;

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

        Language.setLanguage(UserInfoActivity.this, LoginActivity.LANGUAGE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.user_info_title));
        }

        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(UserInfoViewModel.class);

        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        addControl();
        addEvent();
        displayUserInfo();
    }

    private void addControl() {
        imgUserName = findViewById(R.id.imgUserName);
        imgEmail = findViewById(R.id.imgEmail);
        imgAddDate = findViewById(R.id.imgAddDate);
        imgModifyDate = findViewById(R.id.imgModifyDate);

        imgUserName.setImageResource(R.drawable.username_detail);
        imgEmail.setImageResource(R.drawable.email_detail);
        imgAddDate.setImageResource(R.drawable.calendar_detail_user);
        imgModifyDate.setImageResource(R.drawable.calendar_clock_detail);

        layoutSpace = findViewById(R.id.layoutSpace);
        txtSpace = findViewById(R.id.txtSpace);

        btnChangeUser = findViewById(R.id.btnChangeUser);

        circleImageView = findViewById(R.id.imgUser);
        txtDisplayName = findViewById(R.id.txtDisplayName);
        txtUserName = findViewById(R.id.txtUserName);
        txtEmail = findViewById(R.id.txtEmail);
        txtAddDate = findViewById(R.id.txtAddDate);
        txtModifyDate = findViewById(R.id.txtModifyDate);
    }

    private void addEvent() {
        layoutSpace.setOnClickListener(this);
        txtSpace.setOnClickListener(this);

        btnChangeUser.setOnClickListener(clickChangeUser);
    }

    private void displayUserInfo() {
        Intent intent = getIntent();
        USER_ID = intent.getIntExtra("userId", -1);
        String path = intent.getStringExtra("path");
        String username = intent.getStringExtra("username");
        String displayName = intent.getStringExtra("displayName");
        String email = intent.getStringExtra("email");
        String dateAdd = intent.getStringExtra("dateAdd");
        String dateModify = intent.getStringExtra("dateModify");

        try {
            byte[] imgArray = getByteArray(path);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgArray, 0, imgArray.length);
            circleImageView.setImageBitmap(bitmap);
        } catch (Exception ex) {
            Log.e("ERROR", ex.getMessage());
        }
        txtUserName.setText(username);
        txtDisplayName.setText(displayName);
        txtEmail.setText(email);
        txtAddDate.setText(dateAdd);
        txtModifyDate.setText(dateModify);

        if (USER_ID == MainActivity.USER_ID) {
            btnChangeUser.setVisibility(View.GONE);
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
            edtUserName.setText(txtUserName.getText());
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
                    String username = String.valueOf(edtUserName.getText());
                    String password = String.valueOf(edtPassword.getText());
                    boolean isValid = mViewModel.isValidPassword(username, password);
                    if (isValid) {
                        MainActivity.USER_ID = USER_ID;
                        dialog.dismiss();
                        Toast.makeText(UserInfoActivity.this, "Đang đăng nhập...", Toast.LENGTH_SHORT).show();

                        mProgress = new ProgressDialog(UserInfoActivity.this);
                        mProgress.setMessage("Đang tải dữ liệu...");
                        mProgress.setCancelable(false);
                        mProgress.show();

                        Intent refresh = new Intent(UserInfoActivity.this, MainActivity.class);
                        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        refresh.putExtra("USER_ID", USER_ID);
                        startActivity(refresh);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            createNotificationChannel();
                        }

                        finish();
                    } else {
                        Toast.makeText(UserInfoActivity.this, "Mật khẩu của bạn không đúng!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialog.show();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {

        String displayName = "bạn";
        for (UserModel user : MainActivity.users) {
            if (user.getUserId() == USER_ID) {
                displayName = user.getDisplayName();
                break;
            }
        }

//        String name = "Bạn đã thêm các khoản thu, chi của bạn chưa?";
//        String description = "Nhấn để mở ứng dụng";

        String name = String.join(" ", "Chào mừng", displayName, "đã quay trở lại!");
        String description = "Hôm nay bạn có các khoản thu, chi gì?";

//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        int notifyID = 1;
        String CHANNEL_ID = "MY_CHANNEL";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.iconapp);
        Notification notification = new Notification.Builder(UserInfoActivity.this)
                .setContentTitle(name)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_baseline_money_24).setLargeIcon(bm)
                .setChannelId(CHANNEL_ID)
//                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(mChannel);

        mNotificationManager.notify(notifyID, notification);
    }
}