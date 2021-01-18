package com.uit.quanlychitieu.adapter;

import android.content.Context;
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
import com.uit.quanlychitieu.model.IncomeModel;
import com.uit.quanlychitieu.model.UserModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class UserItemAdapter extends RecyclerView.Adapter<UserItemAdapter.UserItemViewHolder> {

    private List<UserModel> listUser;
    private OnClickRecycleView mOnClickListener;

    public UserItemAdapter(List<UserModel> listUser) {
        this.listUser = listUser;
    }

    public void setOnClickRecycleView(OnClickRecycleView listener) {
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public UserItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserItemViewHolder(view);
    }

    public UserModel getItem(int position) {
        if (listUser != null) {
            return listUser.get(position);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull UserItemViewHolder holder, int position) {
        UserModel user = listUser.get(position);
        if (user == null) {
            return;
        }
        if (user.bitmap != null) {
            holder.imgUserItem.setImageBitmap(user.bitmap);
        } else {
            holder.imgUserItem.setImageResource(R.drawable.username_detail);
        }

        holder.txtDisplayNameItem.setText(user.getDisplayName());
        holder.txtUserNameItem.setText(user.getUserName());
    }

    @Override
    public int getItemCount() {
        if (listUser != null) {
            return listUser.size();
        }
        return 0;
    }

    public class UserItemViewHolder extends RecyclerView.ViewHolder {

        private View view;

        @BindView(R.id.imgUserItem)
        ImageView imgUserItem;

        @BindView(R.id.txtDisplayNameItem)
        TextView txtDisplayNameItem;

        @BindView(R.id.txtUserNameItem)
        TextView txtUserNameItem;

        public UserItemViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            ButterKnife.bind(this, itemView);
        }

        @OnClick
        void onClick(View view) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(listUser.get(getAdapterPosition()), getAdapterPosition());
            }
        }
    }
}

