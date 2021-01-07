package com.uit.quanlychitieu.adapter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.uit.quanlychitieu.DetailIncomeExpanseActivity;
import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.event.OnClickRecycleView;
import com.uit.quanlychitieu.event.OnLongClickRecycleView;
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.model.ExpenseModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;

public class ExpenseItemAdapter extends RecyclerView.Adapter<ExpenseItemAdapter.ExpesneItemViewHolder> {

    private List<ExpenseModel> listExpense;
    private OnClickRecycleView mOnClickListener;
    private OnLongClickRecycleView mOnLongClickListener;

    public ExpenseItemAdapter(List<ExpenseModel> listExpense) {
        this.listExpense = listExpense;
    }

    public void setOnClickRecycleView(OnClickRecycleView listener) {
        mOnClickListener = listener;
    }

    public void setOnLongClickRecycleView(OnLongClickRecycleView listener) {
        mOnLongClickListener = listener;
    }

    @NonNull
    @Override
    public ExpesneItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpesneItemViewHolder(view);
    }

    public ExpenseModel getItem(int position) {
        if (listExpense != null) {
            return listExpense.get(position);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ExpesneItemViewHolder holder, int position) {
        ExpenseModel expense = listExpense.get(position);
        if (expense == null) {
            return;
        }
        if (expense.bitmap != null) {
            holder.imgCategoty.setImageBitmap(expense.bitmap);
        } else {
            holder.imgCategoty.setImageResource(R.drawable.ei5);
        }
        holder.txtCategory.setText(expense.categoryName);
        holder.txtDate.setText(expense.dateFormated);
        holder.txtTime.setText(expense.timeFormated);
        holder.txtMoney.setText(expense.moneyFormated);
    }

    @Override
    public int getItemCount() {
        if (listExpense != null) {
            return listExpense.size();
        }
        return 0;
    }

    public class ExpesneItemViewHolder extends RecyclerView.ViewHolder {

        private View view;

        @BindView(R.id.imgECategory)
        ImageView imgCategoty;

        @BindView(R.id.txtECategory)
        TextView txtCategory;

        @BindView(R.id.txtEDate)
        TextView txtDate;

        @BindView(R.id.txtEMoney)
        TextView txtMoney;

        @BindView(R.id.txtETime)
        TextView txtTime;

        public ExpesneItemViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            ButterKnife.bind(this, itemView);
        }

        @OnClick
        void onClick(View view) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(listExpense.get(getAdapterPosition()), getAdapterPosition());
            }
        }

        @OnLongClick
        boolean onLongClick(View view) {
            if (mOnLongClickListener != null) {
                mOnLongClickListener.onLongClick(listExpense.get(getAdapterPosition()), getAdapterPosition());
            }
            return true;
        }

    }
}
