package com.uit.quanlychitieu;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.uit.quanlychitieu.ui.login.LoginActivity;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Language.setLanguage(SettingsActivity.this, LoginActivity.LANGUAGE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        // các control trong màn hình cài đặt
        public SwitchPreference switch_reminder;
        public SwitchPreference switch_advertise;
        public ListPreference languages;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            setPreferencesFromResource(R.xml.preferences, rootKey);
            addControl();
            addEvents();
        }

        private void addControl() {
            languages = findPreference("pref_language");
            switch_reminder = findPreference("switch_reminder");
            switch_advertise = findPreference("switch_advertise");
        }

        private void addEvents() {
            languages.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String stringValue = newValue.toString();
                    int index = 0;
                    index = languages.findIndexOfValue(stringValue);
                    preference.setSummary(index >= 0 ? languages.getEntries()[index] : null);
                    saveSettingsInfo(0, index == 0 ? true : false);
                    return true;
                }
            });

            switch_reminder.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    saveSettingsInfo(1, (Boolean) newValue);
                    switch_reminder.setChecked((Boolean) newValue);
                    return false;
                }
            });

            switch_advertise.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    saveSettingsInfo(2, (Boolean) newValue);
                    switch_advertise.setChecked((Boolean) newValue);
                    return false;
                }
            });
        }

        //Lấy thông tin cài đặt
        private void saveSettingsInfo(int i, boolean newValue) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            switch (i) {
                case 0:
                    editor.putString("language", newValue == true ? "vn" : "en");
                    break;
                case 1:
                    editor.putBoolean("reminder", newValue);
                    break;
                case 2:
                    editor.putBoolean("advertise", newValue);
                    break;
            }
            editor.commit();
            notificationText();
        }

        //Thông báo cài đặt đã được thay đổi, cần khởi động lại để áp dụng
        private void notificationText() {
            Toast.makeText(getActivity(), "Khởi động lại để áp dụng cài đặt", Toast.LENGTH_SHORT).show();
        }
    }
}