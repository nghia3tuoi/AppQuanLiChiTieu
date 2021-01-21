package com.uit.quanlychitieu.ui.user.info;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uit.quanlychitieu.R;

import org.w3c.dom.Text;

public class UserInfoFragment extends Fragment {

    private UserInfoViewModel mViewModel;
    private TextView txtUserName, txtDisplayName, txtEmail, txtDateAdd, txtDateModify;
    private Button btnDeleteUser;

    public static UserInfoFragment newInstance() {
        return new UserInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_info_fragment, container, false);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtDisplayName = view.findViewById(R.id.txtDisplayName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtDateAdd = view.findViewById(R.id.txtDateAdd);
        txtDateModify = view.findViewById(R.id.txtDateModify);
        btnDeleteUser = view.findViewById(R.id.btnDeleteUser);
        btnDeleteUser.setOnClickListener(clickDeleteUser);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UserInfoViewModel.class);

        mViewModel.getUserName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                txtUserName.setText(s);
            }
        });

        mViewModel.getDisplayName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                txtDisplayName.setText(s);
            }
        });

        mViewModel.getEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                txtEmail.setText(s);
            }
        });

        mViewModel.getDateAdd().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                txtDateAdd.setText(s);
            }
        });

        mViewModel.getDateModify().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                txtDateModify.setText(s);
            }
        });
    }

    private final View.OnClickListener clickDeleteUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.dialog_question);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (dialog != null && dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            final TextView txt_Titleconfirm = dialog.findViewById(R.id.txt_Titleconfirm);
            final Button btnYes = dialog.findViewById(R.id.btnYes);
            final Button btnNo = dialog.findViewById(R.id.btnNo);

            txt_Titleconfirm.setText("Tất cả thông tin và dữ liệu người dùng sẽ bị xóa, bạn chắc chắn?");
            btnNo.setText("Hủy bỏ");
            btnYes.setText("Đồng ý");

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewModel.deleteUser();
                    Toast.makeText(getActivity(), "Xóa người dùng thành công", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    Activity activity = getActivity();
                    Intent intent = activity.getBaseContext().getPackageManager().getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            dialog.show();
        }
    };
}