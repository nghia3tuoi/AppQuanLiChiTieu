package com.uit.quanlychitieu.ui.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.uit.quanlychitieu.ui.login.LoginActivity;
import com.uit.quanlychitieu.ui.user.edituser.EditUserActivity;
import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.model.UserModel;
import com.uit.quanlychitieu.ui.user.info.UserInfoFragment;
import com.uit.quanlychitieu.ui.user.listuser.ListUserFragment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment {

    private UserViewModel mViewModel;

    private static final int REQUEST_CODE = 95;

    private ViewPager viewPager;
    private UserFragment.SectionsPagerAdapter sectionsPagerAdapter;
    private TabLayout tabLayout;

    private UserModel user;
    private TextView txtDisplayName, txtExpenseMoney, txtIncomeMoney;

    private ImageView imgEdit;
    private CircleImageView imgUser;
    private Button btnRemoveData;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(UserViewModel.class);
        View root = inflater.inflate(R.layout.fragment_user, container, false);

        for (UserModel user : LoginActivity.users) {
            if (user.getUserId() == MainActivity.USER_ID) {
                this.user = user;
                break;
            }
        }

        btnRemoveData = root.findViewById(R.id.btnRemoveData);
        btnRemoveData.setOnClickListener(clickRemoveData);

        imgUser = root.findViewById(R.id.imgUser);
        txtDisplayName = root.findViewById(R.id.txtDisplayName);

        imgEdit = root.findViewById(R.id.imgEdit);
        imgEdit.setOnClickListener(clickEditUserInfo);

        txtExpenseMoney = root.findViewById(R.id.txtExpenseMoney);
        txtIncomeMoney = root.findViewById(R.id.txtIncomeMoney);

        displayUserInfo();

        sectionsPagerAdapter = new UserFragment.SectionsPagerAdapter(getChildFragmentManager());
        viewPager = root.findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout = root.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        mViewModel.getTotalMoneyExpense().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                txtExpenseMoney.setText(s);
            }
        });

        mViewModel.getTotalMoneyIncome().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                txtIncomeMoney.setText(s);
            }
        });

        return root;
    }

    private void displayUserInfo() {
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-mm-yyyy");
        txtDisplayName.setText(user != null ? user.getDisplayName() : getResources().getString(R.string.user_welcomes));
        imgUser.setImageBitmap(user.bitmap);
    }

    private final View.OnClickListener clickEditUserInfo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), EditUserActivity.class);
            String filePath = saveTempFileImageAsByteArray(getContext(), user.getImageAvatar(), "avatar");
            intent.putExtra("path", filePath);
            intent.putExtra("userName", user.getUserName());
            intent.putExtra("displayName", user.getDisplayName());
            intent.putExtra("email", user.getEmail());

            Pair<View, String> p = Pair.create(imgUser, "imgUser");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p);
            startActivityForResult(intent, REQUEST_CODE, options.toBundle());
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == EditUserActivity.RESULT_CODE) {
            String newDisplayName = data.getStringExtra("newDisplayName");
            String newEmail = data.getStringExtra("newEmail");
            String pathImg = data.getStringExtra("path");
            String newDateModify = data.getStringExtra("dateModify");

            user.setDisplayName(newDisplayName);
            user.setEmail(newEmail);
            user.setDateModify(newDateModify);
            try {
                byte[] img = getByteArray(pathImg);
                user.setImageAvatar(img);
            } catch (Exception ex) {
                Log.e("ERROR", ex.getMessage());
            }
            user.formatData();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
    }

    private byte[] getByteArray(String filePath) throws IOException {
        File file = new File(filePath);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
        buf.read(bytes, 0, bytes.length);
        buf.close();
        return bytes;
    }

    private String saveTempFileImageAsByteArray(Context context, byte[] img, String name) {
        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name);
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            os.write(img);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("ERORR", "Error writing file", e);
        }
        return imageFile.getAbsolutePath();
    }

    private final View.OnClickListener clickRemoveData = new View.OnClickListener() {
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

            txt_Titleconfirm.setText(getResources().getString(R.string.user_question_cleardata));
            btnNo.setText(getResources().getString(R.string.expense_income_cancel));
            btnYes.setText(getResources().getString(R.string.user_ok));

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewModel.clearDataUser();
                    Toast.makeText(getActivity(), getResources().getString(R.string.user_all_data_deleted), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    };

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = new UserInfoFragment();
            } else if (position == 1) {
                fragment = new ListUserFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.user_information);
                case 1:
                    return getResources().getString(R.string.user_list);
            }
            return null;
        }
    }
}