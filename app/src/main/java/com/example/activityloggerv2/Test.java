package com.example.activityloggerv2;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.activityloggerv2.charts.QuadrantChart;
import com.example.activityloggerv2.model.DataProcessing;

import androidx.annotation.RequiresApi;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class Test extends Fragment {


    DataProcessing data;
    int CHART_ID = 3;
    LinearLayout graph_layout;
    QuadrantChart q_chart;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_charts, container, false);
        graph_spinner(v);
        data = new DataProcessing();
        graph_layout = v.findViewById(R.id.graph_layout);
        clear_chart();
        LayoutInflater inflator = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        q_chart = new QuadrantChart(data, graph_layout, inflator);
        return v;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Visualize");
    }

    public void graph_spinner(View view) {
        Spinner spinner = view.findViewById(R.id.graph_selection);
        final String[] types_graph = {"Quadrant", "Goals"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, types_graph);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String select = (String) parent.getItemAtPosition(pos);
                switch (select) {
                    case "Quadrant":
                        clear_chart();
                        q_chart.quadrantChart();
                        break;
                    case "Goals":
                        clear_chart();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void clear_chart() {
        if (graph_layout.findViewById(CHART_ID) != null) {
            ((ViewGroup) graph_layout.findViewById(CHART_ID).getParent()).removeAllViews();
        }
    }


}
