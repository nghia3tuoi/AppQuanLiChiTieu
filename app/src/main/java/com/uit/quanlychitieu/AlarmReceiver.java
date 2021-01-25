package com.uit.quanlychitieu;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.uit.quanlychitieu.model.UserModel;
import com.uit.quanlychitieu.ui.login.LoginActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        createNotificationChannel(context);
//        Calendar calendar = Calendar.getInstance();
//        DateFormat format = SimpleDateFormat.getDateInstance();
//        Toast.makeText(context, format.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
    }

    //Tạo một thông báo khi người dùng đăng nhập thành công
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(Context context) {

        String name = "Bạn đã thêm các khoản thu, chi của bạn chưa?";
        String description = "Nhấn để mở ứng dụng";

        Intent intent = new Intent(context, LoginActivity .class);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        int notifyID = 2;
        String CHANNEL_ID = "MY_CHANNEL";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.iconapp);
        Notification notification = new Notification.Builder(context)
                .setContentTitle(name)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_baseline_money_24).setLargeIcon(bm)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(mChannel);

        mNotificationManager.notify(notifyID, notification);
    }
}
