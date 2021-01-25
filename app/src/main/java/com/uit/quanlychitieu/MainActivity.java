package com.uit.quanlychitieu;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.ObservableArrayList;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.model.ExpenseModel;
import com.uit.quanlychitieu.model.IncomeModel;
import com.uit.quanlychitieu.model.UserModel;
import com.uit.quanlychitieu.ui.login.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    // Người dùng đăng nhập hiện tại
    public static int USER_ID = 1;

    private PendingIntent mAlarmIntent;

    // Tên cở sở dữ liệu
    public static String DATABASE_NAME = "QuanLyChiTieu.db";
    public static String DB_PATH_SUFFIX = "/databases/";
    public static SQLiteDatabase database = null;

    //Dữ liệu được load từ database
    public static ObservableArrayList<ExpenseModel> expenses;
    public static ObservableArrayList<IncomeModel> incomes;
    public static ObservableArrayList<CategoryModel> categoryExpanses;
    public static ObservableArrayList<CategoryModel> categoryIncomes;
    public static ObservableArrayList<UserModel> users;

    //Các control trên màn hình chính
    private NavigationView navigationView;

    private AppBarConfiguration mAppBarConfiguration;

    //Quảng cáo dưới màn hình
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Language.setLanguage(MainActivity.this, LoginActivity.LANGUAGE);
        MainActivity.super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        USER_ID = intent.getIntExtra("USER_ID", 0);

        try {
            categoryExpanses = loadDataCategotyFromDatabase("DanhMucChiTieu");
            categoryIncomes = loadDataCategotyFromDatabase("DanhMucThuNhap");
            expenses = loadDataExpenseFromDatabase();
            incomes = loadDataIncomeFromDatabase();

            users = loadDataUser();
            saveImgToDatabase();
        } catch (Exception ex) {
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_expense, R.id.nav_income, R.id.nav_statistic,
                R.id.nav_user, R.id.nav_category_spending)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_settings:
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                }
                NavigationUI.onNavDestinationSelected(menuItem, navController);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        requestPermissions();
        initAd();
        setNotificationReminder();
    }

    private void setNotificationReminder() {
        if (LoginActivity.isNotification) {
            Intent intent = new Intent(this, AlarmReceiver.class);
            mAlarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            long interval = 10000;

            Toast.makeText(this, getResources().getString(R.string.turn_on_notification), Toast.LENGTH_SHORT).show();
            manager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + interval, interval, mAlarmIntent);
        }
    }

    @Override
    public void onBackPressed() {
        showDialogQuestionCloseApp();
    }

    //Mở dialog hỏi người dùng muốn đóng ứng dụng không
    private void showDialogQuestionCloseApp() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_question);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        final TextView txt_Titleconfirm = dialog.findViewById(R.id.txt_Titleconfirm);
        final Button btnYes = dialog.findViewById(R.id.btnYes);
        final Button btnNo = dialog.findViewById(R.id.btnNo);

        txt_Titleconfirm.setText(getResources().getString(R.string.main_question_exits));
        btnNo.setText(getResources().getString(R.string.add_dialog_back));
        btnYes.setText(getResources().getString(R.string.user_ok));

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //Copy cở sở dữ liệu từ project đến dứng dụng nếu không tồn tại
    private void processCopy() {
        try {
            File dbFile = getDatabasePath(DATABASE_NAME);
            if (!dbFile.exists()) {
                copyDatabaseFromAsset();
                Toast.makeText(MainActivity.this, R.string.copy_success, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
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

    // Load dữ liệu các khoản chi tiêu từ cơ sở dữ liệu
    public ObservableArrayList<ExpenseModel> loadDataExpenseFromDatabase() {
        database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        ObservableArrayList<ExpenseModel> expenseList = new ObservableArrayList<>();
        Cursor cursor = database.rawQuery("select * from ChiTieu where UserId = " + USER_ID, null);
        while (cursor.moveToNext()) {
            int transactionId = cursor.getInt(0);
            int categoryId = cursor.getInt(1);
            String transactionDate = cursor.getString(2);
            String transactionTime = cursor.getString(3);
            int transactionMoney = cursor.getInt(4);
            String note = cursor.getString(5);
            int userId = cursor.getInt(6);
            ExpenseModel expense = new ExpenseModel(transactionId, categoryId, transactionDate, transactionTime, transactionMoney, note, userId);
            expenseList.add(expense);
        }
        cursor.close();
        return expenseList;
    }

    // Load dữ liệu các khoản thu nhập từ cơ sở dữ liệu
    public ObservableArrayList<IncomeModel> loadDataIncomeFromDatabase() {
        database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        ObservableArrayList<IncomeModel> incomeList = new ObservableArrayList<>();
        Cursor cursor = database.rawQuery("select * from ThuNhap where UserId = " + USER_ID, null);
        while (cursor.moveToNext()) {
            int transactionId = cursor.getInt(0);
            int categoryId = cursor.getInt(1);
            String transactionDate = cursor.getString(2);
            String transactionTime = cursor.getString(3);
            int transactionMoney = cursor.getInt(4);
            String note = cursor.getString(5);
            int userId = cursor.getInt(6);

            IncomeModel income = new IncomeModel(transactionId, categoryId, transactionDate, transactionTime, transactionMoney, note, userId);
            incomeList.add(income);
        }
        cursor.close();
        return incomeList;
    }

    // Load dữ liệu danh mục (Thu nhập hoặc Chi tiêu) từ cơ sở dữ liệu
    private ObservableArrayList<CategoryModel> loadDataCategotyFromDatabase(String table) {
        ObservableArrayList<CategoryModel> list = new ObservableArrayList<>();
        database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("select * from " + table, null);
        while (cursor.moveToNext()) {
            int categoryId = cursor.getInt(0);
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            byte[] imageCategory = cursor.getBlob(3);

            CategoryModel category = new CategoryModel(categoryId, name, description, imageCategory);
            list.add(category);
        }
        cursor.close();
        return list;
    }

    //Cập nhật ảnh vào database
    public void updateImage(String nameTable, int id, Bitmap img) {
        if (database == null) {
            database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        }
        String idName = "";
        String columnName = "";
        switch (nameTable) {
            case "DanhMucThuNhap":
            case "DanhMucChiTieu":
                columnName = "ImageCategory";
                idName = "CategoryId";
                break;
            case "User":
                columnName = "ImageAvatar";
                idName = "UserId";
                break;
        }
        byte[] data = getBitmapAsByteArray(img);
        ContentValues values = new ContentValues();
        values.put(columnName, data);
        int result = database.update(nameTable, values, idName + "=?", new String[]{id + ""});
        if (result > 0) {
            Toast.makeText(this, "Đã cập nhật ảnh", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(this, "Có lỗi khi cập nhật ảnh", Toast.LENGTH_SHORT);
        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    //Lưu ảnh vào database
    private void saveImgToDatabase() {
        int numberOfCategotySample = 5;
        int numberOfUserSample = 3;
        int[] idExpenses = {R.drawable.e1, R.drawable.e2, R.drawable.e3, R.drawable.e4, R.drawable.ei5};
        int[] idIncomes = {R.drawable.i1, R.drawable.i2, R.drawable.i3, R.drawable.i4, R.drawable.ei5};
        //Lưu ảnh danh mục thu nhập vào database
        for (int i = 0; i < numberOfCategotySample; i++) {
            if (categoryIncomes.get(i).getImageCategory() == null) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), idIncomes[i]);
                updateImage("DanhMucThuNhap", i + 1, bitmap);
            }
        }
        //Lưu ảnh danh mục chi tiêu vào database
        for (int i = 0; i < numberOfCategotySample; i++) {
            if (categoryExpanses.get(i).getImageCategory() == null) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), idExpenses[i]);
                updateImage("DanhMucChiTieu", i + 1, bitmap);
            }
        }
        //Lưu ảnh đại diện người dùng vào database
        for (int i = 0; i < numberOfUserSample; i++) {
            if (users.get(i).getImageAvatar() == null) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.username_detail);
                updateImage("User", i + 1, bitmap);
            }
        }
    }

    // Load dữ liệu người dùng từ cơ sở dữ liệu
    private ObservableArrayList<UserModel> loadDataUser() {
        if (database == null) {
            database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        }
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

    //Khởi tạo quảng có
    private void initAd() {
        adView = findViewById(R.id.ad_view);
        if (LoginActivity.isDisplayAd) {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345")).build());

            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        } else {
            adView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private void requestPermissions() {
        //Yêu cầu Permission tại đây.
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.
                request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                                .setTitle(getResources().getString(R.string.notify_access_denied))
                                .setMessage(getResources().getString(R.string.notify_advise))
                                .setPositiveButton(getResources().getString(R.string.user_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MainActivity.this.finish();
                                    }
                                }).setCancelable(false);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
    }
}


