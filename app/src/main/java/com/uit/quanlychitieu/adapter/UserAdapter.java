package com.uit.quanlychitieu.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableList;

import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.databinding.ItemUserBinding;
import com.uit.quanlychitieu.model.IncomeModel;
import com.uit.quanlychitieu.model.UserModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends ArrayAdapter<UserModel> {
    private ObservableList<UserModel> dataSet;
    private Context context;

    private ItemUserBinding binding;
    public UserAdapter(ObservableList<UserModel> data, Context context) {
        super(context, R.layout.item_income, data);
        this.dataSet = data;
        this.context = context;
    }

    @Nullable
    @Override
    public UserModel getItem(int position) {
        return dataSet.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_user, parent, false);
        binding.setUserItem(getItem(position));
        return binding.getRoot();

//        LayoutInflater inflater = this.context.getLayoutInflater();
//        View customView = inflater.inflate(this.resource, null);
//
//        CircleImageView imgUserItem = customView.findViewById(R.id.imgUserItem);
//        TextView txtDisplayNameItem = customView.findViewById(R.id.txtDisplayNameItem);
//        TextView txtUserNameItem = customView.findViewById(R.id.txtUserNameItem);
//
//        UserModel user = getItem(position);
//        if (user.getImageAvatar() != null) {
//            Bitmap bitmap = BitmapFactory.decodeByteArray(user.getImageAvatar(), 0, user.getImageAvatar().length);
//            imgUserItem.setImageBitmap(bitmap);
//        } else {
//            imgUserItem.setImageResource(R.drawable.avatar);
//        }
//        txtDisplayNameItem.setText(user.getDisplayName());
//        txtUserNameItem.setText(user.getUserName());
//        return customView;
    }
}
