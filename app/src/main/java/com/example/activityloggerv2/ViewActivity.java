package com.example.activityloggerv2;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.activityloggerv2.model.UserActivities;


/**
 * Created by Belal on 18/09/16.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class ViewActivity extends Fragment {


    final long TOTAL_SECONDS = 500000000;
    final long INTERVAL_SECONDS = 60;
    TextView counter;
    UserActivities user_activities;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.view_activity, container, false);
        user_activities = ViewModelProviders.of(getActivity()).get(UserActivities.class);
        user_activities.refresh();
        CountDownTimer timer = new CountDownTimer(TOTAL_SECONDS * 1000, INTERVAL_SECONDS * 1000) {
            public void onTick(long millisUntilFinished) {
                TextView counter = (TextView) v.findViewById(R.id.counter);
                int dura = user_activities.duration();
                int hr = dura / 60;
                int min = dura % 60;
                counter.setText(Integer.toString(hr) + " hrs " + Integer.toString(min) + " M");
            }

            public void onFinish() {
            }

        };
        timer.start();
        refresh_spinner(v);
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Current Activity");


    }

    public void refresh_spinner(View view) {
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, user_activities.loaded_activities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        counter = (TextView) view.findViewById(R.id.counter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String select = (String) parent.getItemAtPosition(pos);
                if (select != null) {
                    if (!select.contentEquals(user_activities.getCurrent())) {
                        user_activities.log_activity();
                        user_activities.setCurrent(select);
                        counter.setText("0 M");
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}