package com.example.activityloggerv2;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.activityloggerv2.model.UserActivities;


public class AddActivity extends Fragment {

    UserActivities user_activities;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.add_activity, container, false);
        user_activities = ViewModelProviders.of(getActivity()).get(UserActivities.class);
        final String[] type = {""};
        final int[] mins = {0};
        Spinner spinner = v.findViewById(R.id.type_activity);
        Button add_button = v.findViewById(R.id.add_button);
        final EditText editText = v.findViewById(R.id.activity);
        NumberPicker hr =  v.findViewById(R.id.hr);
        NumberPicker min =  v.findViewById(R.id.min);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String select = (String) parent.getItemAtPosition(pos);
                if (select != null) {
                    if (select.contains("Q1")){
                        type[0] =  "Q1";
                    }
                    if (select.contains("Q2")){
                        type[0] = "Q2";
                    }
                    if (select.contains("Q3")){
                        type[0] = "Q3";
                    }
                    if (select.contains("Q4")){
                        type[0] = "Q4";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        hr.setMinValue(0);
        hr.setMaxValue(100);
        min.setMinValue(0);
        min.setMaxValue(59);

        hr.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                mins[0] = newVal*60;
            }
        });


        min.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                mins[0] += newVal;
            }
        });


        add_button.setOnClickListener(new OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View view) {
                if (editText == null){
                    Toast.makeText(getActivity().getApplicationContext(),"Null",Toast.LENGTH_SHORT).show();
                }
                String message = editText.getText().toString();
                user_activities.save_activities(message+","+ type[0]+","+Integer.toString(mins[0]));
                editText.setText("");
            }
        });

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Add Activity");
    }

}
