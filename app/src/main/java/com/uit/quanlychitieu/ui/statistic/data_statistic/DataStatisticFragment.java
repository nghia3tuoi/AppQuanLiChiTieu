package com.uit.quanlychitieu.ui.statistic.data_statistic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uit.quanlychitieu.R;

public class DataStatisticFragment extends Fragment {

    private ImageView imgSelectStartDate, imgSelectEndDate;

    public DataStatisticFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_statistic, container, false);

        imgSelectStartDate = view.findViewById(R.id.imgSelectStartDate);
        imgSelectEndDate = view.findViewById(R.id.imgSelectEndDate);
        imgSelectStartDate.setImageResource(R.drawable.ic_baseline_date_24);
        imgSelectEndDate.setImageResource(R.drawable.ic_baseline_date_24);

        return view;
    }
}