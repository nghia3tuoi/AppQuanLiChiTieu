package com.uit.quanlychitieu.ui.user.adduser;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.databinding.ActivityAddUserBinding;
import com.uit.quanlychitieu.databinding.ActivityEditUserBinding;
import com.uit.quanlychitieu.databinding.ActivityLoginBinding;
import com.uit.quanlychitieu.model.UserModel;
import com.uit.quanlychitieu.ui.login.LoginActivity;
import com.uit.quanlychitieu.ui.login.LoginViewModel;
import com.uit.quanlychitieu.ui.login.LoginViewModelFactory;
import com.uit.quanlychitieu.ui.userinfo.UserInfoActivity;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddUserActivity extends AppCompatActivity implements AddUserCallbacks {

    private AddUserViewModel addUserViewModel;
    private Toolbar toolbar;
    private CircleImageView imgUser;
    public static byte[] bitmapByteArray;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        ActivityAddUserBinding activityAddUserBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_user);
        addUserViewModel = new ViewModelProvider(this, new AddUserViewModelFactory(this)).get(AddUserViewModel.class);
        activityAddUserBinding.setViewModel(addUserViewModel);

//        toolbar = findViewById(R.id.toolbar_id);
//        setSupportActionBar(toolbar);

        imgUser = findViewById(R.id.imgUser);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.username_detail);
        imgUser.setImageBitmap(bitmap);
        bitmapByteArray = MainActivity.getBitmapAsByteArray(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cancel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel:
                finish();
                break;
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
                    bitmapByteArray = MainActivity.getBitmapAsByteArray(selectedImage);
                } catch (Exception ex) {
                    Toast.makeText(this, "Không thể tải hình ảnh!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                return;
        }
    }

    @Override
    public void onSuccess(String message) {

        Toast.makeText(AddUserActivity.this, message, Toast.LENGTH_SHORT).show();

        mProgress = new ProgressDialog(AddUserActivity.this);
        mProgress.setMessage("Thiết lập tài khoản của bạn...");
        mProgress.setCancelable(false);
        mProgress.show();

        int USER_ID = LoginActivity.users.get(LoginActivity.users.size() - 1).getUserId();
        Intent refresh = new Intent(AddUserActivity.this, MainActivity.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        refresh.putExtra("USER_ID", USER_ID);
        startActivity(refresh);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        finish();
    }

    @Override
    public void onFailure(String message) {

        Toast.makeText(AddUserActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChangeImageUser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh đại điện"), 10);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {

        String name = String.join(" ", "Chào mừng bạn đến với ứng dụng quản lý chi tiêu");
        String description = "Giờ đây, bạn đã có thể dễ dàng quản lý các khoản thu, chi của mình";

        int notifyID = 1;
        String CHANNEL_ID = "MY_CHANNEL";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.iconapp);
        Notification notification = new Notification.Builder(AddUserActivity.this)
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