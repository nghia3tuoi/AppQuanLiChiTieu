package com.uit.quanlychitieu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.uit.quanlychitieu.model.ExpenseModel;
import com.uit.quanlychitieu.model.IncomeModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DetailIncomeExpanseActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgCategory, imgDate, imgTime, imgMoney, imgNote;
    private TextView txtCategory, txtTime, txtDate, txtMoney, txtNote;

    //Chạm vào các control này sẽ đóng màn hình
    private LinearLayout layoutSpace;
    private TextView txtSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_income_expanse);

        imgCategory = findViewById(R.id.imgCatogory);
        imgDate = findViewById(R.id.imgDate);
        imgTime = findViewById(R.id.imgTime);
        imgMoney = findViewById(R.id.imgMoney);
        imgNote = findViewById(R.id.imgNote);

        layoutSpace = findViewById(R.id.layoutSpace);
        txtSpace = findViewById(R.id.txtSpace);
        layoutSpace.setOnClickListener(this);
        txtSpace.setOnClickListener(this);

        imgTime.setImageResource(R.drawable.clock_detail);
        imgDate.setImageResource(R.drawable.calendar_detail);
        imgMoney.setImageResource(R.drawable.money);
        imgNote.setImageResource(R.drawable.note_detail);

        txtCategory = findViewById(R.id.txtCategory);
        txtTime = findViewById(R.id.txtTime);
        txtDate = findViewById(R.id.txtDate);
        txtMoney = findViewById(R.id.txtMoney);
        txtNote = findViewById(R.id.txtNote);

        Intent intent = getIntent();
        String filePath = intent.getStringExtra("path");
        String categoryName = intent.getStringExtra("categoryName");
        String dateFormated = intent.getStringExtra("dateFormated");
        String timeFormated = intent.getStringExtra("timeFormated");
        String moneyFormated = intent.getStringExtra("moneyFormated");
        String note = intent.getStringExtra("note");
        displayTextFormated(filePath, categoryName, dateFormated, timeFormated, moneyFormated, note);
    }

    @Override
    public void onClick(View v) {
        finishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    private void displayTextFormated(String filePath, String categoryName, String dateFormated, String timeFormated, String moneyFormated, String note) {
        try {
            byte[] bytes = getByteArray(filePath);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imgCategory.setImageBitmap(bitmap);
        } catch (Exception ex) {
            Log.e("ERORR", ex.getMessage());
        }

        txtCategory.setText(categoryName);
        txtDate.setText(dateFormated);
        txtTime.setText(timeFormated);
        txtMoney.setText(moneyFormated);
        txtNote.setText(note);
    }

    //
    private byte[] getByteArray(String filePath) throws IOException {
        File file = new File(filePath);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
        buf.read(bytes, 0, bytes.length);
        buf.close();
        return bytes;
    }
}