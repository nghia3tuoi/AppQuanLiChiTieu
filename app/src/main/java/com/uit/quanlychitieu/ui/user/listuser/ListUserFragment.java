package com.uit.quanlychitieu.ui.user.listuser;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uit.quanlychitieu.AddUserActivity;
import com.uit.quanlychitieu.DetailIncomeExpanseActivity;
import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.UserInfoActivity;
import com.uit.quanlychitieu.adapter.CategoryItemAdapter;
import com.uit.quanlychitieu.adapter.DividerItem;
import com.uit.quanlychitieu.adapter.UserItemAdapter;
import com.uit.quanlychitieu.event.OnClickRecycleView;
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.model.ExpenseModel;
import com.uit.quanlychitieu.model.UserModel;
import com.uit.quanlychitieu.ui.category.expense_manager.CategoryExpenseViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ListUserFragment extends Fragment implements OnClickRecycleView<UserModel> {

    private ListUserViewModel mViewModel;
    private RecyclerView rcvUser;
    private UserItemAdapter userItemAdapter;
    private LinearLayoutManager linearLayoutManager;

    private List<UserModel> users;
    private FloatingActionButton fab;

    public static ListUserFragment newInstance() {
        return new ListUserFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.list_user_fragment, container, false);

        rcvUser = root.findViewById(R.id.rcvUser);

        mViewModel = new ViewModelProvider(this).get(ListUserViewModel.class);
        users = new ArrayList<>(MainActivity.users);

        rcvUser.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvUser.setLayoutManager(linearLayoutManager);
        userItemAdapter = new UserItemAdapter(users);
        userItemAdapter.setOnClickRecycleView(this);
        rcvUser.setAdapter(userItemAdapter);
        rcvUser.addItemDecoration(new DividerItem(getContext(), DividerItemDecoration.VERTICAL, 36));

        mViewModel.getListUserLiveData().observe(getActivity(), new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
                users.clear();
                users.addAll(userModels);
                rcvUser.setAdapter(userItemAdapter);
            }
        });

        fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddUser = new Intent(getActivity(), AddUserActivity.class);
                startActivity(intentAddUser);
            }
        });

        return root;
    }

    @Override
    public void onClick(UserModel item, int position) {
        try {
            View view = linearLayoutManager.findViewByPosition(position);
            ImageView imgUser = view.findViewById(R.id.imgUserItem);

            UserModel userSelected = userItemAdapter.getItem(position);
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
        } catch (Exception ex) {
            Log.e("ERROR", ex.getMessage());
        }
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
}