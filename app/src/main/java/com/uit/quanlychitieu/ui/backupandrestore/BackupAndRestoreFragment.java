package com.uit.quanlychitieu.ui.backupandrestore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.uit.quanlychitieu.R;

public class BackupAndRestoreFragment extends Fragment {

    private BackupAndRestoreViewModel mViewModel;

    private ImageView imgStorage, imgStorage1, imgStorage2;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(BackupAndRestoreViewModel.class);
        View root = inflater.inflate(R.layout.fragment_backup_and_restore, container, false);
        imgStorage = root.findViewById(R.id.imgStorage);
        imgStorage1 = root.findViewById(R.id.imgStorage1);
        imgStorage2 = root.findViewById(R.id.imgStorage2);
        imgStorage.setImageResource(R.drawable.ic_baseline_sd_storage_24);
        imgStorage1.setImageResource(R.drawable.ic_baseline_sd_storage_24);
        imgStorage2.setImageResource(R.drawable.ic_baseline_sd_storage_24);
        return root;
    }

}