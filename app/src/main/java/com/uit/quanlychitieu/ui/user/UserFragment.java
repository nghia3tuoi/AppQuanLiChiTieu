package com.uit.quanlychitieu.ui.user;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.uit.quanlychitieu.EditUserActivity;
import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.UserInfoActivity;
import com.uit.quanlychitieu.adapter.ExpenseAdapter;
import com.uit.quanlychitieu.adapter.UserAdapter;
import com.uit.quanlychitieu.model.UserModel;
import com.uit.quanlychitieu.ui.category.CategoryViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserFragment extends Fragment {

    private UserViewModel mViewModel;

    private ListView lstUser;
    private UserAdapter adapter;
    private Button btnEditUserInfo, btnRemoveUser;
    private UserModel user;
    private TextView txtDisplayName, txtUserName, txtDateAdd;

    private ImageView imgUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(UserViewModel.class);
        View root = inflater.inflate(R.layout.fragment_user, container, false);

        for (UserModel user : MainActivity.users) {
            if (user.getUserId() == MainActivity.USER_ID) {
                this.user = user;
                break;
            }
        }

        lstUser = root.findViewById(R.id.lsvUser);
        adapter = new UserAdapter(MainActivity.users, getContext());
        lstUser.setAdapter(adapter);
        lstUser.setOnItemClickListener(clickItemUser);

        btnEditUserInfo = root.findViewById(R.id.btnEditInfo);
        btnEditUserInfo.setOnClickListener(clickEditUserInfo);

        btnRemoveUser = root.findViewById(R.id.btnRemoveUser);
        btnRemoveUser.setOnClickListener(clickRemoveUser);

        txtDisplayName = root.findViewById(R.id.txtDisplayName);
        txtUserName = root.findViewById(R.id.txtUserName);
        txtDateAdd = root.findViewById(R.id.txtDateAdd);

        imgUser = root.findViewById(R.id.imgUser);
        DisplayUserInfo();

        return root;
    }

    private void DisplayUserInfo() {
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-mm-yyyy");

        if (user != null) {
            txtDisplayName.setText(user.getDisplayName());
            txtUserName.setText(user.getUserName());

            String sDateAdd = user.getDateAdd();
            Date date = new SimpleDateFormat("dd/mm/yyyy").parse(sDateAdd, new ParsePosition(0));

            txtDateAdd.setText(formatDate.format(date));
        } else {
            txtDisplayName.setText("Chào mừng!");
            txtUserName.setText("Bạn chưa đăng ký");
            txtDateAdd.setText(formatDate.format(new Date().toString()));
        }
    }

    private final AdapterView.OnItemClickListener clickItemUser = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView imgUser = view.findViewById(R.id.imgUserItem);

            UserModel userSelected = adapter.getItem(position);
            Intent intent = new Intent(getActivity(), UserInfoActivity.class);
            String filePath = saveTempFileImageAsByteArray(getContext(), userSelected.getImageAvatar(), "avatar");
            intent.putExtra("path", filePath);
            intent.putExtra("username", userSelected.getUserName());
            intent.putExtra("displayname", userSelected.getDisplayName());
            intent.putExtra("email", userSelected.getEmail());
            intent.putExtra("dateadd", userSelected.dateAddFormated);
            intent.putExtra("datemodify", userSelected.dateModifyFormated);

            Pair<View, String> p = Pair.create(imgUser, "imgUser");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p);
            startActivity(intent, options.toBundle());
        }
    };

    private final View.OnClickListener clickEditUserInfo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent intent = new Intent(getActivity(), EditUserActivity.class);
                String filePath = saveTempFileImageAsByteArray(getContext(), user.getImageAvatar(), "avatar");
                intent.putExtra("path", filePath);
                intent.putExtra("username", user.getUserName());
                intent.putExtra("displayname", user.getDisplayName());
                intent.putExtra("email", user.getEmail());

                //intent.putExtra("user", user);
                Pair<View, String> p = Pair.create(imgUser, "imgUser");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p);
                startActivity(intent, options.toBundle());
            } catch (Exception ex) {
                Log.e("ERORR", ex.getMessage());
            }
        }
    };

    public static String saveTempFileImageAsByteArray(Context context, byte[] img, String name) {
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

    private final View.OnClickListener clickRemoveUser = new View.OnClickListener() {
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
                    Toast.makeText(getActivity(), "Xóa người dùng thành công", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    };
}