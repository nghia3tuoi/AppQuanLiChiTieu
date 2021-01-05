package com.uit.quanlychitieu.ui.backupandrestore;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BackupAndRestoreViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<String> mText;

    public BackupAndRestoreViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery Backup And Restore");
    }

    public LiveData<String> getText() {
        return mText;
    }
}