package com.uit.quanlychitieu.ui.statistic.data_statistic;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.databinding.ActivityAddUserBinding;
import com.uit.quanlychitieu.databinding.FragmentDataStatisticBinding;
import com.uit.quanlychitieu.ui.user.adduser.AddUserViewModel;
import com.uit.quanlychitieu.ui.user.adduser.AddUserViewModelFactory;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataStatisticFragment extends Fragment implements DataStatisticCallbacks {

    private DataStatisticViewModel mViewModel;
    private ImageView imgSelectStartDate, imgSelectEndDate;
    private TextView txtFromDate, txtToDate, txtMoneyExpense, txtMoneyIncome, txtBalance;

    public DataStatisticFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_data_statistic, container, false);

        FragmentDataStatisticBinding fragmentDataStatisticBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_data_statistic, container, false);
        mViewModel = new ViewModelProvider(this, new DataStatisticViewModelFactory(this)).get(DataStatisticViewModel.class);
        fragmentDataStatisticBinding.setViewModel(mViewModel);

        View view = fragmentDataStatisticBinding.getRoot();
        addControl(view);

        imgSelectStartDate.setImageResource(R.drawable.ic_baseline_date_24);
        imgSelectEndDate.setImageResource(R.drawable.ic_baseline_date_24);

        return view;
    }

    private void addControl(View view) {
        imgSelectStartDate = view.findViewById(R.id.imgSelectStartDate);
        imgSelectEndDate = view.findViewById(R.id.imgSelectEndDate);
        txtFromDate = view.findViewById(R.id.txtFromDate);
        txtToDate = view.findViewById(R.id.txtToDate);
        txtMoneyExpense = view.findViewById(R.id.txtMoneyExpense);
        txtMoneyIncome = view.findViewById(R.id.txtMoneyIncome);
        txtBalance = view.findViewById(R.id.txtBalance);
    }

    @Override
    public void onSelectStartDate() {

        Date d = new SimpleDateFormat("dd-MM-yyyy").parse(mViewModel.getStartDate(), new ParsePosition(0));
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //Cập nhật ngày tháng
                SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
                //mViewModel.setStartDate(formatDate.format(cal.getTime()));
                txtFromDate.setText(formatDate.format(cal.getTime()));
            }
        };

        new DatePickerDialog(getActivity(), date,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onSelectEndDate() {

        Date d = new SimpleDateFormat("dd-MM-yyyy").parse(mViewModel.getEndDate(), new ParsePosition(0));
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //Cập nhật ngày tháng
                SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
                //mViewModel.setEndDate(formatDate.format(cal.getTime()));
                txtToDate.setText(formatDate.format(cal.getTime()));
            }
        };

        new DatePickerDialog(getActivity(), date,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onDisplayResult(String totalExpense, String totalIncome, String balance) {
        txtMoneyExpense.setText(totalExpense);
        txtMoneyIncome.setText(totalIncome);
        txtBalance.setText(balance);
    }

}