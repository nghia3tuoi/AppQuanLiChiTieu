package com.uit.quanlychitieu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableList;

import com.uit.quanlychitieu.R;
//import com.uit.quanlychitieu.databinding.ItemIncomeBinding;
import com.uit.quanlychitieu.model.IncomeModel;


public class IncomeAdapter extends ArrayAdapter<IncomeModel> {

    private ObservableList<IncomeModel> dataSet;
    private Context context;

    //private ItemIncomeBinding binding;

    public IncomeAdapter(ObservableList<IncomeModel> data, Context context) {
        super(context, R.layout.item_income, data);
        this.dataSet = data;
        this.context = context;
    }

    @Nullable
    @Override
    public IncomeModel getItem(int position) {
        return dataSet.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_income, parent, false);
//        binding.setIncomeItem(getItem(position));
//        return binding.getRoot();
        return null;
    }
}
