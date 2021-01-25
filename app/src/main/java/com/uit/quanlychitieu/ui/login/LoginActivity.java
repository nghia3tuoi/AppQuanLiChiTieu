package com.uit.quanlychitieu.ui.login;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.ViewModelProvider;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.uit.quanlychitieu.Language;
import com.uit.quanlychitieu.ui.user.adduser.AddUserActivity;
import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.databinding.ActivityLoginBinding;
import com.uit.quanlychitieu.model.UserModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements LoginCallbacks {

    public final static String TAG = "LoginActivity";

    //thông tin cài đặt ứng dụng mặc định
    public static String LANGUAGE = "vn";
    public static boolean isDisplayAd = false;
    public static boolean isNotification = false;

    private ProgressDialog mProgress;
    private CheckBox chkSaveInfo;

    // Danh sách người dùng được load từ cơ sở dữ liệu
    public static ObservableArrayList<UserModel> users;
    private LoginViewModel loginViewModel;

    // Tên cở sở dữ liệu
    public static String DATABASE_NAME = "QuanLyChiTieu.db";
    public static String DB_PATH_SUFFIX = "/databases/";
    public static SQLiteDatabase database = null;

    // Lưu lại ID người dùng khi đăng nhập thành công
    public static int USER_ID_LOGIN = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSettingsInfo();
        Language.setLanguage(LoginActivity.this, LoginActivity.LANGUAGE);
        setTheme(R.style.Theme_QuanLyChiTieu_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Copy cở sở dữ liệu từ project đến dứng dụng nếu không tồn tại
        processCopy();
        users = loadDataUser();

        ActivityLoginBinding activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory(this)).get(LoginViewModel.class);
        activityLoginBinding.setViewModel(loginViewModel);

        chkSaveInfo = findViewById(R.id.chkSave);
        getUserInfo();
    }

    //Lưu lại thông tin người dùng cho lần đăng nhập sau
    private void saveUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userName = String.valueOf(loginViewModel.getUsername());
        String password = String.valueOf(loginViewModel.getPassword());
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

    //Lấy thông tin người dùng cho lần đăng nhập sau
    private void getUserInfo() {

        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        if (sharedPreferences == null) {
            return;
        }
        boolean isSaveInfo = sharedPreferences.getBoolean("isChecked", false);
        if (isSaveInfo) {
            String userName = sharedPreferences.getString("userName", "");
            String password = sharedPreferences.getString("password", "");
            loginViewModel.setUsername(userName);
            loginViewModel.setPassword(password);
        } else {
            loginViewModel.setUsername("");
            loginViewModel.setPassword("");
        }
        chkSaveInfo.setChecked(isSaveInfo);
    }

    //Tạo một thông báo khi người dùng đăng nhập thành công
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {

        String displayName = "bạn";
        for (UserModel user : users) {
            if (user.getUserId() == USER_ID_LOGIN) {
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
        Notification notification = new Notification.Builder(LoginActivity.this)
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

    // Load dữ liệu người dùng từ cơ sở dữ liệu
    private ObservableArrayList<UserModel> loadDataUser() {
        database = openOrCreateDatabase("QuanLyChiTieu.db", MODE_PRIVATE, null);
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
        Log.d(TAG, "processCopy: ");
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

    // Tạo đăng dẫn của file database
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

    // Thực hiện khi người dùng đăng nhập thành công
    @Override
    public void onSuccess(String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
        saveUserInfo();
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
        mProgress = new ProgressDialog(LoginActivity.this);
        mProgress.setMessage("Đang tải dữ liệu...");
        mProgress.setCancelable(false);
        mProgress.show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USER_ID", USER_ID_LOGIN);
        startActivity(intent);
    }

    // Thực hiện khi người dùng đăng nhập thất bại
    @Override
    public void onFailure(String message) {

        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    // Thực hiện khi người dùng nhấn vào Đăng ký
    @Override
    public void onRegister() {
        Intent intent = new Intent(LoginActivity.this, AddUserActivity.class);
        startActivity(intent);
    }


    //Lấy thông tin cài đặt
    private void getSettingsInfo() {

        Log.e(TAG, "getSettingsInfo: ");
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        if (sharedPreferences == null) {
            return;
        }
        String language = sharedPreferences.getString("language", "vn");
        boolean reminder = sharedPreferences.getBoolean("reminder", false);
        boolean advertise = sharedPreferences.getBoolean("advertise", false);
        LANGUAGE = language;
        isNotification = reminder;
        isDisplayAd = advertise;
    }
}