package com.uit.quanlychitieu;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.ObservableArrayList;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.uit.quanlychitieu.adapter.ExpenseItemAdapter;
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.model.ExpenseModel;
import com.uit.quanlychitieu.model.IncomeModel;
import com.uit.quanlychitieu.model.UserModel;
import com.uit.quanlychitieu.ui.category.CategoryFragment;
import com.uit.quanlychitieu.ui.expense.ExpenseFragment;
import com.uit.quanlychitieu.ui.income.IncomeFragment;
import com.uit.quanlychitieu.ui.user.UserFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;

public class MainActivity extends AppCompatActivity {

    // Người dùng đăng nhập hiện tại
    public static int USER_ID = 1;

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
    public static int USER = 1;

    //Các control trên màn hình chính
    private NavigationView navigationView;
    FloatingActionButton fab;

    private AppBarConfiguration mAppBarConfiguration;

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

        txt_Titleconfirm.setText("Bạn có muốn thoát ứng dụng không?");
        btnNo.setText("Trở lại");
        btnYes.setText("Đống ý");

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Copy cở sở dữ liệu từ project đến dứng dụng nếu không tồn tại
        processCopy();

        categoryExpanses = loadDataCategotyFromDatabase("DanhMucChiTieu");
        categoryIncomes = loadDataCategotyFromDatabase("DanhMucThuNhap");
        expenses = loadDataExpenseFromDatabase();
        incomes = loadDataIncomeFromDatabase();

        users = loadDataUser();
        saveImgToDatabase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(clickFabButton);
        fab.hide();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_expense, R.id.nav_income, R.id.nav_statistic, R.id.nav_user,
                R.id.nav_backup_and_restore, R.id.nav_category_spending)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.nav_expense:
                        currentFragment = 0;
                        break;
                    case R.id.nav_income:
                        currentFragment = 1;
                        break;
                    case R.id.nav_user:
                        currentFragment = 2;
                        break;
                    case R.id.nav_category_spending:
                        currentFragment = 3;
                        break;
                    case R.id.nav_settings:
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        currentFragment = -1;
                        break;
                }
                if (currentFragment == 2) {
                    fab.show();
                } else {
                    fab.hide();
                }
                NavigationUI.onNavDestinationSelected(menuItem, navController);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private int currentFragment = 0;

    @Override
    public void onBackPressed() {
        showDialogQuestionCloseApp();
    }

    private final View.OnClickListener clickFabButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (currentFragment) {
                case 0:
                    fab.hide();
                    openAddExpenseDialog();
                    break;
                case 1:
                    fab.hide();
                    openAddIncomeDialog();
                    break;
                case 2:
                    Intent intentAddUser = new Intent(MainActivity.this, AddUserActivity.class);
                    startActivity(intentAddUser);
                    break;
                case 3:
                    fab.hide();
                    //openAddCategoryDialog(false);
                    break;
            }
        }
    };

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
    private ObservableArrayList<ExpenseModel> loadDataExpenseFromDatabase() {
        database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        ObservableArrayList<ExpenseModel> expanseList = new ObservableArrayList<>();
        Cursor cursor = database.rawQuery("select * from ChiTieu where UserId = " + USER, null);
        while (cursor.moveToNext()) {
            int transactionId = cursor.getInt(0);
            int categoryId = cursor.getInt(1);
            String transactionDate = cursor.getString(2);
            String transactionTime = cursor.getString(3);
            int transactionMoney = cursor.getInt(4);
            String note = cursor.getString(5);
            int userId = cursor.getInt(6);
            ExpenseModel expense = new ExpenseModel(transactionId, categoryId, transactionDate, transactionTime, transactionMoney, note, userId);
            expanseList.add(expense);
        }
        cursor.close();
        return expanseList;
    }

    // Load dữ liệu các khoản thu nhập từ cơ sở dữ liệu
    private ObservableArrayList<IncomeModel> loadDataIncomeFromDatabase() {
        database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        ObservableArrayList<IncomeModel> incomeList = new ObservableArrayList<>();
        Cursor cursor = database.rawQuery("select * from ThuNhap where UserId = " + USER, null);
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
                columnName = "ImageCategory";
                idName = "CategoryId";
                break;
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
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
                updateImage("User", i + 1, bitmap);
            }
        }
    }

    // Load dữ liệu ngươi dùng từ cơ sở dữ liệu
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

    private void openAddExpenseDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_expense);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final EditText edtTime = dialog.findViewById(R.id.edtTime);
        final EditText edtDate = dialog.findViewById(R.id.edtDate);
        final EditText edtMoney = dialog.findViewById(R.id.edtMoney);
        final EditText edtNote = dialog.findViewById(R.id.edtNote);
        final Button btnAdd = dialog.findViewById(R.id.btnAdd);
        final Button btnBack = dialog.findViewById(R.id.btnBack);
        final Spinner spinner = dialog.findViewById(R.id.spnCategory);

        //Hiển thị dữ liệu danh mục chi tiêu
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(MainActivity.this, R.layout.support_simple_spinner_dropdown_item);
        for (CategoryModel category : categoryExpanses) {
            adapter.add(category.getName());
        }
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final Calendar cal = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //Cập nhật ngày tháng
                SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
                edtDate.setText(formatDate.format(cal.getTime()));
            }

        };

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                //Cập nhật thời gian
                SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
                edtTime.setText(formatTime.format(cal.getTime()));
            }
        };

        //Lấy dữ liệu thời gian hiện tại
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        edtTime.setText(formatTime.format(cal.getTime()));

        //Lấy dữ liệu ngày tháng hiện tại
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        edtDate.setText(formatDate.format(cal.getTime()));

        //Mở cửa sổ dialog chọn ngày tháng
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, date,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Mở cửa sổ dialog chọn thời gian
        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this, time,
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE), true).show();
            }
        });

        //Đóng cửa sổ dialog
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //Thêm khoản chi và đóng cửa sổ dialog
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Thêm khoản chi tiêu
                dialog.dismiss();
                if (edtMoney.getText().toString() == null || edtMoney.getText().toString() == "") {
                    Toast.makeText(MainActivity.this, "Bạn chưa nhập số tiền", Toast.LENGTH_LONG);
                }
            }
        });

        dialog.show();
    }

    private void openAddIncomeDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_income);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final EditText edtTime = dialog.findViewById(R.id.edtTime);
        final EditText edtDate = dialog.findViewById(R.id.edtDate);
        final EditText edtMoney = dialog.findViewById(R.id.edtMoney);
        final EditText edtNote = dialog.findViewById(R.id.edtNote);
        final Button btnAdd = dialog.findViewById(R.id.btnAdd);
        final Button btnBack = dialog.findViewById(R.id.btnBack);
        final Spinner spinner = dialog.findViewById(R.id.spnCategory);

        //Hiển thị dữ liệu danh mục chi tiêu
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(MainActivity.this, R.layout.support_simple_spinner_dropdown_item);
        for (CategoryModel category : categoryExpanses) {
            adapter.add(category.getName());
        }
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final Calendar cal = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //Cập nhật ngày tháng
                SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
                edtDate.setText(formatDate.format(cal.getTime()));
            }

        };

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                //Cập nhật thời gian
                SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
                edtTime.setText(formatTime.format(cal.getTime()));
            }
        };

        //Lấy dữ liệu thời gian hiện tại
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        edtTime.setText(formatTime.format(cal.getTime()));

        //Lấy dữ liệu ngày tháng hiện tại
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        edtDate.setText(formatDate.format(cal.getTime()));

        //Mở cửa sổ dialog chọn ngày tháng
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, date,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Mở cửa sổ dialog chọn thời gian
        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this, time,
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE), true).show();
            }
        });

        //Đóng cửa sổ dialog
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //Thêm khoản chi và đóng cửa sổ dialog
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Thêm khoản chi tiêu
                dialog.dismiss();
                if (edtMoney.getText().toString() == null || edtMoney.getText().toString() == "") {
                    Toast.makeText(MainActivity.this, "Bạn chưa nhập số tiền", Toast.LENGTH_LONG);
                }
            }
        });

        dialog.show();
    }


}