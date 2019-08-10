package com.example.activityloggerv2.model.charts;

import android.graphics.Color;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.activityloggerv2.model.DataProcessing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;


public class QuadrantChart implements OnChartValueSelectedListener {

    DataProcessing data;
    int CHART_ID = 3;
    LinearLayout graph_layout;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public QuadrantChart(DataProcessing data, LinearLayout graph_layout){
        this.data = data;
        this.graph_layout = graph_layout;
        quadrantChart();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void quadrantChart() {
        float[] q_data = data.percentage_quadrant();
        PieChart chart = new PieChart(graph_layout.getContext());
        ViewGroup.LayoutParams params = graph_layout.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        final int[] MY_COLORS = {Color.parseColor("#CC6600"),
                Color.parseColor("#80FF00"),
                Color.parseColor("#FF9933"),
                Color.parseColor("#FF3333")};
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : MY_COLORS) colors.add(c);

        chart.setId(CHART_ID);
        chart.setLayoutParams(params);
        List<PieEntry> entries = new ArrayList<>();
        graph_layout.addView(chart);
        for (int I = 0; I < 4; I++) {
            if (q_data[I] != 0) {
                entries.add(new PieEntry(q_data[I], "Q" + Integer.toString(I + 1)));
            }
        }
        PieDataSet set = new PieDataSet(entries, "Time spent in each quadrant");
        set.setColors(colors);
        set.setValueTextColor(Color.parseColor("#000000"));
        PieData data = new PieData(set);
        chart.setData(data);
        chart.invalidate();
        chart.setOnChartValueSelectedListener(this);
    }



    @Override
    public void onValueSelected(Entry e, Highlight h) {
        PieEntry a = (PieEntry) e;
        Toast.makeText(graph_layout.getContext(),a.getLabel(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }
}
