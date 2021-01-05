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
import com.uit.quanlychitieu.databinding.ItemExpenseBinding;
import com.uit.quanlychitieu.model.ExpenseModel;

public class ExpenseAdapter extends ArrayAdapter<ExpenseModel> {
    private ObservableList<ExpenseModel> dataSet;
    private Context context;

    private ItemExpenseBinding binding;

    public ExpenseAdapter(ObservableList<ExpenseModel> data, Context context) {
        super(context, R.layout.item_expense, data);

        this.dataSet = data;
        this.context = context;
    }

    @Nullable
    @Override
    public ExpenseModel getItem(int position) {
        return dataSet.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_expense, parent, false);
        binding.setExpenseItem(getItem(position));
        return binding.getRoot();
    }
}