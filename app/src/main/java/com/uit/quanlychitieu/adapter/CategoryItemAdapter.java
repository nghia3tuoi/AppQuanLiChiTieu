package com.uit.quanlychitieu.adapter;

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
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.model.ExpenseModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class CategoryItemAdapter extends RecyclerView.Adapter<CategoryItemAdapter.CategoryItemViewHolder> {

    private List<CategoryModel> listCategory;
    private OnClickRecycleView mOnClickListener;

    public CategoryItemAdapter(List<CategoryModel> listCategory) {
        this.listCategory = listCategory;
    }

    public void setOnClickRecycleView(OnClickRecycleView listener) {
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public CategoryItemAdapter.CategoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryItemViewHolder(view);
    }

    public CategoryModel getItem(int position) {
        if (listCategory != null) {
            return listCategory.get(position);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryItemViewHolder holder, int position) {
        CategoryModel category = listCategory.get(position);
        if (category == null) {
            return;
        }
        if (category.bitmap != null) {
            holder.imgCategoty.setImageBitmap(category.bitmap);
        } else {
            holder.imgCategoty.setImageResource(R.drawable.ei5);
        }
        holder.txtCategoryName.setText(category.getName());
        holder.txtCategoryDesciption.setText(category.getDescription());
    }

    @Override
    public int getItemCount() {
        if (listCategory != null) {
            return listCategory.size();
        }
        return 0;
    }

    public class CategoryItemViewHolder extends RecyclerView.ViewHolder {

        private View view;

        @BindView(R.id.imgCategory)
        ImageView imgCategoty;

        @BindView(R.id.txtCategoryName)
        TextView txtCategoryName;

        @BindView(R.id.txtCategoryDesciption)
        TextView txtCategoryDesciption;

        public CategoryItemViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            ButterKnife.bind(this, itemView);
        }

        @OnClick
        void onClick(View view) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(listCategory.get(getAdapterPosition()), getAdapterPosition());
            }
        }

    }
}
