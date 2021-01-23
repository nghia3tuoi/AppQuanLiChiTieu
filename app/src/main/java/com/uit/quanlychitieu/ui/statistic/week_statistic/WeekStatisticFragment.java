package com.uit.quanlychitieu.ui.statistic.week_statistic;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.databinding.FragmentCategoryStatisticBinding;
import com.uit.quanlychitieu.databinding.FragmentWeekStatisticBinding;
import com.uit.quanlychitieu.ui.statistic.category_statistic.CategoryStatisticViewModel;
import com.uit.quanlychitieu.ui.statistic.category_statistic.CategoryStatisticViewModelFactory;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeekStatisticFragment extends Fragment implements WeekStatisticCallbacks {

    private Typeface mTf;
    private LineChart chart;
    private TextView txtStartDate;
    private WeekStatisticViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.fragment_week_statistic, container, false);

        FragmentWeekStatisticBinding fragmentWeekStatisticBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_week_statistic, container, false);
        mViewModel = new ViewModelProvider(this, new WeekStatisticViewModelFactory(this)).get(WeekStatisticViewModel.class);
        fragmentWeekStatisticBinding.setViewModel(mViewModel);

        View view = fragmentWeekStatisticBinding.getRoot();

        txtStartDate = view.findViewById(R.id.txtStartDate);
        chart = view.findViewById(R.id.lineChart);

        // holder.chart.setValueTypeface(mTf);

        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(mViewModel.getStartDate(), new ParsePosition(0)));
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
         setAxits(dayOfWeek);

        // this replaces setStartAtZero(true)

        mViewModel.dataChanged();
        return view;
    }

    private void setAxits(int dayOfWeek) {

        String[] dayOfWeeks = new String[]{"Chủ nhật", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"};
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(5, false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(5, false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);

        final List<String> xLabel = new ArrayList<>();

        int i = dayOfWeek;
        while (xLabel.size() < 7) {
            xLabel.add(dayOfWeeks[i - 1]);
            i++;
            if (i > 7) {
                i = 1;
            }
        }

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xLabel.get((int) value % xLabel.size());
            }
        });
    }

    private LineData generateDataLine(int dayOfWeek, Integer[] expenses, Integer[] incomes) {

        ArrayList<Entry> values1 = new ArrayList<>();
        int i = 0;
        for (Integer expense : expenses) {
            values1.add(new Entry(i, expense));
            i++;
        }

        LineDataSet d1 = new LineDataSet(values1, "Chi tiêu");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

        ArrayList<Entry> values2 = new ArrayList<>();

        i = 0;
        for (Integer income : incomes) {
            values2.add(new Entry(i, income));
            i++;
        }

        LineDataSet d2 = new LineDataSet(values2, "Thu nhập");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
        sets.add(d2);

        return new LineData(sets);
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
                txtStartDate.setText(formatDate.format(cal.getTime()));
            }
        };

        new DatePickerDialog(getActivity(), date,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onDataChanged(int dayOfWeek, Integer[] expenses, Integer[] incomes) {
        if (chart != null) {
            setAxits(dayOfWeek);
            LineData lineData = generateDataLine(dayOfWeek, expenses, incomes);
            chart.setData(lineData);
            chart.animateY(2000);
        }
    }
}