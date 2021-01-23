package com.uit.quanlychitieu.ui.statistic.category_statistic;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.databinding.FragmentCategoryStatisticBinding;
import com.uit.quanlychitieu.databinding.FragmentDataStatisticBinding;
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.model.ExpenseModel;
import com.uit.quanlychitieu.ui.statistic.data_statistic.DataStatisticViewModel;
import com.uit.quanlychitieu.ui.statistic.data_statistic.DataStatisticViewModelFactory;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class CategoryStatisticFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback, CategoryStatisticCallbacks, OnChartValueSelectedListener {

    private CategoryStatisticViewModel mViewModel;
    private Spinner spnType;
    private TextView txtFromDate, txtToDate;

    private static final int PERMISSION_STORAGE = 0;

    protected Typeface tfRegular;
    protected Typeface tfLight;

    private PieChart chart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // View view = inflater.inflate(R.layout.fragment_category_statistic, container, false);

        FragmentCategoryStatisticBinding fragmentDataStatisticBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_statistic, container, false);
        mViewModel = new ViewModelProvider(this, new CategoryStatisticViewModelFactory(this)).get(CategoryStatisticViewModel.class);
        fragmentDataStatisticBinding.setViewModel(mViewModel);

        View view = fragmentDataStatisticBinding.getRoot();
        addControl(view);

        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mViewModel.setType("ChiTieu");
                } else {
                    mViewModel.setType("ThuNhap");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Date from = new SimpleDateFormat("dd-MM-yyyy").parse(mViewModel.getStartDate(), new ParsePosition(0));
        Date to = new SimpleDateFormat("dd-MM-yyyy").parse(mViewModel.getEndDate(), new ParsePosition(0));

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setCenterTextTypeface(tfLight);
        chart.setCenterText(generateCenterSpannableText());

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        chart.setOnChartValueSelectedListener(this);

        //chart.animateY(1400, Easing.EaseInOutQuad);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);

        return view;
    }

    private void addControl(View view) {
        txtFromDate = view.findViewById(R.id.txtFromDate);
        txtToDate = view.findViewById(R.id.txtToDate);
        spnType = view.findViewById(R.id.spnType);
        chart = view.findViewById(R.id.pieChart);
    }

    private void setData(String[] parties, Integer[] values) {
        chart.setCenterText(generateCenterSpannableText());
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < values.length; i++) {
            entries.add(new PieEntry(values[i], parties[i], null));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Danh mục");

        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.COLORFUL_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.LIBERTY_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.PASTEL_COLORS) {
            colors.add(c);
        }

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);

        chart.highlightValues(null);
        chart.invalidate();
        chart.animateY(1400, Easing.EaseInOutQuad);
    }


    private SpannableString generateCenterSpannableText() {
        //Định dạng ngày tháng
        String type = mViewModel.getType().equals("ChiTieu") ? "Chi tiêu" : "Thu nhập";
        StringBuilder period = new StringBuilder("Từ ");
        period.append(mViewModel.getStartDate()).append(" đến ").append(mViewModel.getEndDate());
        SpannableString s = new SpannableString(type + "\n" + period);
        s.setSpan(new RelativeSizeSpan(1.7f), 0, type.length(), 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), type.length(), s.length() - type.length() + 1, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), type.length(), s.length(), 0);
        s.setSpan(new RelativeSizeSpan(1f), type.length(), s.length(), 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
    }

    @Override
    public void onNothingSelected() {
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
    public void onDataChanged(String[] parties, Integer[] values) {
        setData(parties, values);
    }
}