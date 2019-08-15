package com.example.activityloggerv2.charts;

import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.activityloggerv2.R;
import com.example.activityloggerv2.model.DataProcessing;
import com.example.activityloggerv2.model.LogData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;


public class QuadrantChart implements OnChartValueSelectedListener {

    DataProcessing data;
    int CHART_ID = 3;
    LinearLayout graph_layout;
    LayoutInflater inflator;
    final int[] MY_COLORS = {Color.parseColor("#CC6600"),
            Color.parseColor("#80FF00"),
            Color.parseColor("#FF9933"),
            Color.parseColor("#FF3333")};

    @RequiresApi(api = Build.VERSION_CODES.O)
    public QuadrantChart(DataProcessing data, LinearLayout graph_layout, LayoutInflater inflator) {
        this.data = data;
        this.graph_layout = graph_layout;
        this.inflator = inflator;
        quadrantChart();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void quadrantChart() {
        float[] q_data = data.percentage_quadrant();
        PieChart chart = new PieChart(graph_layout.getContext());
        ViewGroup.LayoutParams params = graph_layout.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        PieEntry a = (PieEntry) e;
        create_popup(a.getLabel());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void create_bar_chart(LinearLayout view, String name, int ind) {
        BarChart chart = new BarChart(view.getContext());

        List<BarEntry> entries = new ArrayList<>();
        HashMap<String, LogData> activity_data = data.get_activities(name);
        Iterator<Map.Entry<String, LogData>> iter = activity_data.entrySet().iterator();
        float index = 0f;
        final ArrayList<String> xLabel = new ArrayList<>();
        final ArrayList<Integer> color = new ArrayList<Integer>();
        while (iter.hasNext()) {
            Map.Entry<String, LogData> entry = iter.next();
            entries.add(new BarEntry(index, (float) (entry.getValue().getSpent()) / 60));
            xLabel.add(entry.getKey());
            index += 1.0;
        }
        BarDataSet set = new BarDataSet(entries, name + " activities");
        set.setColor(MY_COLORS[ind]);
        BarData data = new BarData(set);
        data.setBarWidth(0.9f);
        chart.setData(data);

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        chart.setLayoutParams(params);
        chart.setFitBars(false);

        view.addView(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                float decimal = value - (int) value;
                if (decimal == 0.0)
                    return xLabel.get(Math.round(value));
                else
                    return "";
            }
        });
        chart.invalidate();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void create_popup(String graphTitle) {
        final View popupView = inflator.inflate(R.layout.activity_table_frag, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(graph_layout, Gravity.CENTER, 0, 0);

        LinearLayout tableContainer = popupView.findViewById(R.id.activity_table);
        int ind = 0;
        if (graphTitle.contentEquals("Q2")) ind = 1;
        if (graphTitle.contentEquals("Q3")) ind = 2;
        if (graphTitle.contentEquals("Q4")) ind = 3;
        create_bar_chart(tableContainer, graphTitle, ind);
        TextView title = popupView.findViewById(R.id.activity_title);
        title.setText(graphTitle);
        Button close = popupView.findViewById(R.id.activity_frag_close);
        close.setOnClickListener(new View.OnClickListener() {

            @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public void onNothingSelected() {

    }
}
