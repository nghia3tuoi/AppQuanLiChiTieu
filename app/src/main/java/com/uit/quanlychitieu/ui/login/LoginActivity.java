package com.uit.quanlychitieu.ui.login;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.ViewModelProvider;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.Observable;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.uit.quanlychitieu.AddUserActivity;
import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.model.UserModel;
import com.uit.quanlychitieu.ui.expense.ExpenseViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtUserName, edtPassword;
    private ImageView imgLogo;
    private Button btnLogin;
    private TextView txtRegister;
    private ProgressDialog mProgress;
    private CheckBox chkSaveInfo;

    public static ObservableArrayList<UserModel> users;
    private LoginViewModel loginViewModel;

    // Tên cở sở dữ liệu
    public static String DATABASE_NAME = "QuanLyChiTieu.db";
    public static String DB_PATH_SUFFIX = "/databases/";
    public static SQLiteDatabase database = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_QuanLyChiTieu_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Copy cở sở dữ liệu từ project đến dứng dụng nếu không tồn tại
        processCopy();
        users = loadDataUser();
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        imgLogo = findViewById(R.id.imgLogo);
        imgLogo.setImageResource(R.drawable.iconapp);

        chkSaveInfo = findViewById(R.id.chkSave);
        edtUserName = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        getUserInfo();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String userName = String.valueOf(edtUserName.getText());
                String password = String.valueOf(edtPassword.getText());

                int USER_ID_LOGIN = loginViewModel.isValid(userName, password);
                if (USER_ID_LOGIN != -1) {
                    createNotificationChannel();

                    saveUserInfo();
                    mProgress = new ProgressDialog(LoginActivity.this);
                    mProgress.setMessage("Đang tải dữ liệu...");
                    mProgress.setCancelable(false);
                    mProgress.show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                    intent.putExtra("USER_ID", USER_ID_LOGIN);

                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Tên người dùng hoặc mật khẩu không hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtRegister = findViewById(R.id.txtRegister);
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AddUserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userName = String.valueOf(edtPassword.getText());
        String password = String.valueOf(edtPassword.getText());
        boolean isSaveInfo = chkSaveInfo.isChecked();
        if (!isSaveInfo) {
            editor.clear();
        } else {
            editor.putString("userName", userName);
            editor.putString("password", password);
            editor.putBoolean("isChecked", isSaveInfo);
        }
        editor.commit();
    }

    private void getUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);

        if (sharedPreferences == null) {
            return;
        }
        boolean isSaveInfo = sharedPreferences.getBoolean("isChecked", false);
        if (isSaveInfo) {
            String userName = sharedPreferences.getString("userName", "");
            String password = sharedPreferences.getString("password", "");
            edtUserName.setText(userName);
            edtPassword.setText(password);
        } else {
            edtUserName.setText("");
            edtPassword.setText("");
        }
        chkSaveInfo.setChecked(isSaveInfo);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {

        String name = "Bạn đã thêm các khoản thu, chi của bạn chưa?";
        String description = "Nhấn để mở ứng dụng";

        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        int notifyID = 1;
        String CHANNEL_ID = "MY_CHANNEL";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        Notification notification = new Notification.Builder(LoginActivity.this)
                .setContentTitle(name)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_baseline_money_24)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(mChannel);

        mNotificationManager.notify(notifyID, notification);
    }

    // Load dữ liệu người dùng từ cơ sở dữ liệu
    private ObservableArrayList<UserModel> loadDataUser() {
        SQLiteDatabase database = openOrCreateDatabase("QuanLyChiTieu.db", MODE_PRIVATE, null);
        ObservableArrayList<UserModel> userList = new ObservableArrayList<>();
        Cursor cursor = database.rawQuery("select * from User", null);
        while (cursor.moveToNext()) {
            int userId = cursor.getInt(0);
            String displayName = cursor.getString(1);
            String userName = cursor.getString(2);
            String password = cursor.getString(3);
            String email = cursor.getString(4);
            byte[] imageAvatar = cursor.getBlob(5);
            String dateAdd = cursor.getString(6);
            String dateModify = cursor.getString(7);
            UserModel user = new UserModel(userId, displayName, userName, password, email, imageAvatar, dateAdd, dateModify);
            userList.add(user);
        }
        cursor.close();
        return userList;
    }

    //Copy cở sở dữ liệu từ project đến dứng dụng nếu không tồn tại
    private void processCopy() {
        try {
            File dbFile = getDatabasePath(DATABASE_NAME);
            if (!dbFile.exists()) {
                copyDatabaseFromAsset();
                Toast.makeText(LoginActivity.this, R.string.copy_success, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(LoginActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
            Log.e("Error", ex.toString());
        }
    }

    private String getDatabasePath() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }

    private void copyDatabaseFromAsset() {
        try {
            InputStream inputStream = getAssets().open(DATABASE_NAME);
            String outputFileName = getDatabasePath();
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists()) {
                f.mkdir();
            }
            OutputStream outputStream = new FileOutputStream(outputFileName);
            byte[] buffer = new byte[1024];
            int lenght;
            while ((lenght = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, lenght);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (Exception ex) {
            Log.e("Error", ex.toString());
        }
    }
}