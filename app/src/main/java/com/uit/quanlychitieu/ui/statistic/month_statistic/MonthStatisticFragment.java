package com.uit.quanlychitieu.ui.statistic.month_statistic;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.databinding.FragmentDataStatisticBinding;
import com.uit.quanlychitieu.databinding.FragmentMonthStatisticBinding;
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.ui.statistic.data_statistic.DataStatisticViewModel;
import com.uit.quanlychitieu.ui.statistic.data_statistic.DataStatisticViewModelFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MonthStatisticFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener, MonthStatisticCallbacks {

    private BarChart chart;
    private SeekBar seekBarX;
    private TextView tvX;

    //Chia độ rộng và khoảng cách các cột trong biểu đồ
    float groupSpace = 0.12f;
    float barSpace = 0.06f; // x4 DataSet
    float barWidth = 0.38f; // x4 DataSet
    // (0.2 + 0.03) * 4 + 0.08 = 1.00 -> interval per "group"

    //
    int groupCount = 12;
    int startMonth = 1;
    int endMonth = 13;

    protected Typeface tfRegular;
    protected Typeface tfLight;
    private Spinner spnYear;

    private MonthStatisticViewModel mViewModel;

    public MonthStatisticFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {

            FragmentMonthStatisticBinding fragmentDataStatisticBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_month_statistic, container, false);
            mViewModel = new ViewModelProvider(this, new MonthStatisticViewModelFactory(this)).get(MonthStatisticViewModel.class);
            fragmentDataStatisticBinding.setViewModel(mViewModel);

            View view = fragmentDataStatisticBinding.getRoot();
            tvX = view.findViewById(R.id.txtXMax);

            spnYear = view.findViewById(R.id.spnYear);
            setDataSpinner();
            spnYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mViewModel.setPositionSelected(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            seekBarX = view.findViewById(R.id.seekBarMonth);
            seekBarX.setOnSeekBarChangeListener(this);

            chart = view.findViewById(R.id.barChart);
            chart.setOnChartValueSelectedListener(this);
            chart.getDescription().setEnabled(false);


//        chart.setDrawBorders(true);

            chart.setPinchZoom(false);
            chart.setDrawBarShadow(false);
            chart.setDrawGridBackground(false);
            seekBarX.setProgress(12);

            Legend l = chart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(true);
            l.setTypeface(tfLight);
            l.setYOffset(0f);
            l.setXOffset(10f);
            l.setYEntrySpace(0f);
            l.setTextSize(4f);

            final XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTypeface(tfLight);
            xAxis.setGranularity(1f);
            xAxis.setCenterAxisLabels(true);

            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return super.getFormattedValue(value);
                }
            });

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setTypeface(tfLight);
            leftAxis.setValueFormatter(new LargeValueFormatter());
            leftAxis.setDrawGridLines(false);
            leftAxis.setSpaceTop(35f);
            leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

            updateXAxis(12);

            chart.getAxisRight().setEnabled(false);
            return view;
        } catch (Exception ex) {
            Log.e("ERROR", ex.getMessage());
            return inflater.inflate(R.layout.fragment_month_statistic, container, false);
        }

    }

    private void setDataSpinner() {
        if (spnYear != null) {
            //Hiển thị dữ liệu danh mục chi tiêu
            ArrayAdapter<String> adapterCategory;
            adapterCategory = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item);
            List<Integer> years = mViewModel.getYears();
            for (Integer year : years) {
                adapterCategory.add(getResources().getString(R.string.statisctic_year) + String.valueOf(year));
            }

            adapterCategory.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spnYear.setAdapter(adapterCategory);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        groupCount = seekBarX.getProgress();
        updateXAxis(groupCount);
        startMonth = 1;
        endMonth = startMonth + groupCount;
        tvX.setText(progress + getResources().getString(R.string.statistic_month));

        MonthStatisticViewModel.DataBarChart data = mViewModel.loadDataBarChar();
        if (data != null) {
            setData(data.getMoneyExpense(), data.getMoneyIncome());
        }
    }

    private void updateXAxis(int progress) {
        if (chart != null) {
            XAxis xAxis = chart.getXAxis();
            String[] months = getXLable(progress);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        }
    }

    private String[] getXLable(int numberOfMonth) {

        String thang1 = getResources().getString(R.string.january);
        String thang2 = getResources().getString(R.string.february);
        String thang3 = getResources().getString(R.string.march);
        String thang4 = getResources().getString(R.string.april);
        String thang5 = getResources().getString(R.string.may);
        String thang6 = getResources().getString(R.string.june);
        String thang7 = getResources().getString(R.string.july);
        String thang8 = getResources().getString(R.string.august);
        String thang9 = getResources().getString(R.string.september);
        String thang10 = getResources().getString(R.string.october);
        String thang11 = getResources().getString(R.string.november);
        String thang12 = getResources().getString(R.string.december);
        String thang2_3 = getResources().getString(R.string.feb_mar);
        String thang4_5 = getResources().getString(R.string.apr_may);
        String thang6_7 = getResources().getString(R.string.jun_jul);
        String thang8_9 = getResources().getString(R.string.aug_sep);
        String thang10_11 = getResources().getString(R.string.oct_nov);


        switch (numberOfMonth) {
            case 9:
            case 10:
                String[] months1 = new String[]{thang1, "", thang2_3, "", thang4_5, "", thang6_7, "", thang8_9, "", thang10};
                return months1;
            case 11:
            case 12:
                String[] months2 = new String[]{thang1, "", thang2_3, "", thang4_5, "", thang6_7, "", thang8_9, "", thang10_11, "", thang12};
                return months2;
            default:
                String[] months3 = new String[]{"", thang1, thang2, thang3, thang4, thang5, thang6, thang7, thang8};
                return months3;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private void setData(Integer[] expenses, Integer[] incomes) {

        if (chart != null) {
            int numberOfMonth = seekBarX.getProgress();
            ArrayList<BarEntry> values1 = new ArrayList<>();
            ArrayList<BarEntry> values2 = new ArrayList<>();

            for (int i = startMonth - 1; i < numberOfMonth; i++) {
                values1.add(new BarEntry(i, (float) expenses[i]));
                values2.add(new BarEntry(i, (float) incomes[i]));
            }

            BarDataSet set1, set2;

            if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {

                set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
                set2 = (BarDataSet) chart.getData().getDataSetByIndex(1);
                set1.setValues(values1);
                set2.setValues(values2);
                chart.getData().notifyDataChanged();
                chart.notifyDataSetChanged();

            } else {
                set1 = new BarDataSet(values1, getResources().getString(R.string.statisctics_income));
                set1.setColor(Color.rgb(104, 241, 175));
                set2 = new BarDataSet(values2, getResources().getString(R.string.statistics_expense));
                set2.setColor(Color.rgb(255, 102, 0));

                BarData data = new BarData(set1, set2);
                data.setValueFormatter(new LargeValueFormatter());
                data.setValueTypeface(tfLight);

                chart.setData(data);
            }

            chart.getBarData().setBarWidth(barWidth);

            chart.getXAxis().setAxisMinimum(startMonth);
            int v = (int) (startMonth + chart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);

            chart.getXAxis().setAxisMaximum(v);
            chart.groupBars(startMonth, groupSpace, barSpace);
            chart.animateY(2000);
            chart.invalidate();
        }
    }

    @Override
    public void onDataChanged(Integer[] expenses, Integer[] incomes) {
        setData(expenses, incomes);
    }
}