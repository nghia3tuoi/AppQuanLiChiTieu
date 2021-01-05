package com.uit.quanlychitieu.ui.statistic.week_statistic;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;
import java.util.List;

public class WeekStatisticFragment extends Fragment {

    private Typeface mTf;
    LineChart chart;

    public WeekStatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_week_statistic, container, false);
        // apply styling
        // holder.chart.setValueTypeface(mTf);
        chart = view.findViewById(R.id.chart);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);

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
        xLabel.add("Thứ 2");
        xLabel.add("Thứ 3");
        xLabel.add("Thứ 4");
        xLabel.add("Thứ 5");
        xLabel.add("Thứ 6");
        xLabel.add("Thứ 7");
        xLabel.add("Chủ nhật");

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xLabel.get((int) value % xLabel.size());
            }
        });
        // this replaces setStartAtZero(true)

        // set data
        Integer[] expenses = new Integer[]{20,30,40,70,30,10, 70};
        Integer[] incomes = new Integer[]{50,30,80,10,20,40, 20};
        chart.setData(generateDataLine(expenses, incomes));

        // do not forget to refresh the chart
        // holder.chart.invalidate();
        chart.animateY(2000);

        return view;
    }

    private LineData generateDataLine(Integer[] expenses, Integer[] incomes) {

        ArrayList<Entry> values1 = new ArrayList<>();

        int i = 0;
        String[] dayOfWeek = new String[]{"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
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
}