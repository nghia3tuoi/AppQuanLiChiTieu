package com.uit.quanlychitieu.ui.statistic.category_statistic;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
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
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.model.ExpenseModel;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CategoryStatisticFragment extends Fragment implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener {

//    protected final String[] parties = new String[]{
//            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
//            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
//            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
//            "Party Y", "Party Z"
//    };

    private static final int PERMISSION_STORAGE = 0;

    protected Typeface tfRegular;
    protected Typeface tfLight;

    private PieChart chart;
    private SeekBar seekBarX;
    private TextView tvX;

    public CategoryStatisticFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_statistic, container, false);

        tvX = view.findViewById(R.id.tvXMax);
        seekBarX = view.findViewById(R.id.seekBar1);
        seekBarX.setOnSeekBarChangeListener(this);
        chart = view.findViewById(R.id.chart1);

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        Calendar cal = Calendar.getInstance();
        cal.set(2020, 6, 7);
        Date from = cal.getTime();
        cal.set(2020, 12, 22);
        Date to = cal.getTime();

        chart.setCenterTextTypeface(tfLight);
        chart.setCenterText(generateCenterSpannableText(true, from, to));

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

        seekBarX.setProgress(5);

        chart.animateY(1400, Easing.EaseInOutQuad);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);

        Integer[] values = new Integer[5];
        int k = 0;
        for (ExpenseModel expense : MainActivity.expenses) {
            values[k] = expense.getExpenseMoney();
            k++;
        }
        Arrays.sort(values, Collections.reverseOrder());
        String[] parties = new String[values.length];
        int i = 0;
        for (CategoryModel category : MainActivity.categoryExpanses) {
            parties[i] = category.getName();
            i++;
        }
        setData(parties, values);

        seekBarX.setMax(parties.length);
        return view;
    }

    private void setData(String[] parties, Integer[] values) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < values.length; i++) {
            entries.add(new PieEntry(values[i], parties[i], null));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Loại thu chi");

        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);
        chart.invalidate();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tvX.setText(progress + " mục");
        //chart.animateY(1400, Easing.EaseInOutQuad);
    }

    private SpannableString generateCenterSpannableText(Boolean isExpense, Date from, Date to) {
        //Định dạng ngày tháng
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String type = isExpense == true ? "Chi tiêu" : "Thu nhập";
        StringBuilder period = new StringBuilder("Từ ");
        period.append(dateFormat.format(from)).append(" đến ").append(dateFormat.format(to));
        SpannableString s = new SpannableString(type + "\n" + period);
        s.setSpan(new RelativeSizeSpan(1.7f), 0, type.length(), 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), type.length(), s.length() - type.length() + 1, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), type.length(), s.length(), 0);
        s.setSpan(new RelativeSizeSpan(1f), type.length(), s.length(), 0);
        return s;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        StringBuilder message = new StringBuilder("Bạn vừa chọn ");
        float k = Float.parseFloat(String.valueOf(h.getX()));
        int i = 0;
        for (CategoryModel category : MainActivity.categoryExpanses) {
            if (i == (int) k) {
                message.append(category.getName()).append(".");
            }
            i++;
        }
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {
    }
}