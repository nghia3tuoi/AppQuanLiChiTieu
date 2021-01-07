package com.uit.quanlychitieu.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.event.OnClickRecycleView;
import com.uit.quanlychitieu.event.OnLongClickRecycleView;
import com.uit.quanlychitieu.model.ExpenseModel;
import com.uit.quanlychitieu.model.IncomeModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class IncomeItemAdapter extends RecyclerView.Adapter<IncomeItemAdapter.IncomeItemViewHolder> {

    private List<IncomeModel> listIncome;
    private OnClickRecycleView mOnClickListener;
    private OnLongClickRecycleView mOnLongClickListener;

    public IncomeItemAdapter(List<IncomeModel> listIncome, Context context) {
        this.listIncome = listIncome;
    }

    public void setOnClickRecycleView(OnClickRecycleView listener) {
        mOnClickListener = listener;
    }

    public void setOnLongClickRecycleView(OnLongClickRecycleView listener) {
        mOnLongClickListener = listener;
    }

    @NonNull
    @Override
    public IncomeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income, parent, false);
        return new IncomeItemViewHolder(view);
    }

    public IncomeModel getItem(int position) {
        if (listIncome != null) {
            return listIncome.get(position);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeItemViewHolder holder, int position) {
        IncomeModel income = listIncome.get(position);
        if (income == null) {
            return;
        }
        if (income.bitmap != null) {
            holder.imgCategoty.setImageBitmap(income.bitmap);
        } else {
            holder.imgCategoty.setImageResource(R.drawable.ei5);
        }

        holder.txtCategory.setText(income.categoryName);
        holder.txtDate.setText(income.dateFormated);
        holder.txtTime.setText(income.timeFormated);
        holder.txtMoney.setText(income.moneyFormated);
    }

    @Override
    public int getItemCount() {
        if (listIncome != null) {
            return listIncome.size();
        }
        return 0;
    }

    public class IncomeItemViewHolder extends RecyclerView.ViewHolder {

        private View view;

        @BindView(R.id.imgICategory)
        ImageView imgCategoty;

        @BindView(R.id.txtICategory)
        TextView txtCategory;

        @BindView(R.id.txtIDate)
        TextView txtDate;

        @BindView(R.id.txtIMoney)
        TextView txtMoney;

        @BindView(R.id.txtITime)
        TextView txtTime;

        public IncomeItemViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            ButterKnife.bind(this, itemView);
        }

        @OnClick
        void onClick(View view) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(listIncome.get(getAdapterPosition()), getAdapterPosition());
            }
        }

        @OnLongClick
        boolean onLongClick(View view) {
            if (mOnLongClickListener != null) {
                mOnLongClickListener.onLongClick(listIncome.get(getAdapterPosition()), getAdapterPosition());
            }
            return true;
        }

    }

}
