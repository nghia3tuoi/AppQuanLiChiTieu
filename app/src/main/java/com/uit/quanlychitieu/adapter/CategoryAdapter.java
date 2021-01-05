package com.uit.quanlychitieu.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.model.CategoryModel;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<CategoryModel> {
    private ArrayList<CategoryModel> dataSet;
    private Context context;

    public CategoryAdapter(ArrayList<CategoryModel> data, Context context) {
        super(context, R.layout.item_category, data);
        this.dataSet = data;
        this.context = context;
    }

    @Nullable
    @Override
    public CategoryModel getItem(int position) {
        return dataSet.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        Activity activity = (Activity) inflater.getContext();
        View customView = inflater.inflate(R.layout.item_category, null);

        ImageView imgCategory = customView.findViewById(R.id.imgCategory);
        TextView txtCategoryName = customView.findViewById(R.id.txtCategoryName);
        TextView txtCategoryDesciption = customView.findViewById(R.id.txtCategoryDesciption);

        CategoryModel category = getItem(position);
        byte[] imgArray = category.getImageCategory();
        if (imgArray != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgArray, 0, imgArray.length);
            imgCategory.setImageBitmap(bitmap);
        } else {
            imgCategory.setImageResource(R.drawable.avatar);
        }
        txtCategoryName.setText(category.getName());
        txtCategoryDesciption.setText(category.getDescription());
        return customView;
    }
}
