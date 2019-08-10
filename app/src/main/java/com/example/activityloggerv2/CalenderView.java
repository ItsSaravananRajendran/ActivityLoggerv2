package com.example.activityloggerv2;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.activityloggerv2.SyncScrollView.ObservableScrollView;
import com.example.activityloggerv2.SyncScrollView.ScrollViewListener;
import com.example.activityloggerv2.model.ManageCalender;
import com.example.activityloggerv2.model.UserActivities;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class CalenderView extends Fragment implements ScrollViewListener {

    private ObservableScrollView scrollView1 = null;
    private ObservableScrollView scrollView2 = null;
    private boolean interceptScroll = true;
    private TextView recyclableTextView;
    UserActivities user_activities;
    String activity_to_add = "";
    String date = "";
    ManageCalender weekly_cal;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        final View v = inflater.inflate(R.layout.calender, container, false);
        user_activities = ViewModelProviders.of(getActivity()).get(UserActivities.class);
        scrollView1 = (ObservableScrollView) v.findViewById(R.id.header_view);
        scrollView1.setScrollViewListener(this);
        scrollView2 = (ObservableScrollView) v.findViewById(R.id.content_view);
        scrollView2.setScrollViewListener(this);
        createTable(v);
        return v;
    }

    public void refresh_frag(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void createTable(View v){
        TableRow.LayoutParams wrapWrapTableRowParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int fixedColumnWidths = 20;
        int scrollableColumnWidths = 20;
        int fixedRowHeight = 70;
        int fixedHeaderHeight = 100;

        weekly_cal = new ManageCalender(date);
        String[] header_data = weekly_cal.get_header();

        TableLayout header =  v.findViewById(R.id.table_header);
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(wrapWrapTableRowParams);
        row.setGravity(Gravity.CENTER);
        row.setBackgroundColor(Color.parseColor("#0C9B37"));

        TextView day = makeTableRowWithText("Day",10,fixedHeaderHeight);
        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_date_pop_up();
            }
        });
        row.addView(day);
        for(int I=0;I<7;I++){
            row.addView(makeTableRowWithText(header_data[I], fixedColumnWidths, fixedHeaderHeight));
        }
        header.addView(row);
        TableLayout fixedColumn =  v.findViewById(R.id.fixed_column);
        TableLayout scrollablePart =  v.findViewById(R.id.scrollable_part);
        LocalTime time= LocalTime.parse("00:00", DateTimeFormatter.ofPattern("H:mm"));

        String color;
        for(int i = 0; i < weekly_cal.getNO_ROWS(); i++) {
            TextView fixedView = makeTableRowWithText(time.toString(), 10,fixedRowHeight);
            if(i%2 == 0){
                fixedView.setBackgroundColor(Color.parseColor("#eeeeee"));
                color = "#ffffff";
            } else{
                fixedView.setBackgroundColor(Color.parseColor("#10D44B"));
                color = "#24DF5C";
            }
            fixedColumn.addView(fixedView);
            row = new TableRow(getContext());
            row.setLayoutParams(wrapWrapTableRowParams);
            row.setGravity(Gravity.CENTER);
            for(int j=0; j<7 ; j++){
                final TextView cell = makeTableRowWithText(weekly_cal.get_calendar_data(i,j), scrollableColumnWidths, fixedRowHeight);
                final int finalJ = j;
                final int finalI = i;
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(weekly_cal.get_calendar_data(finalI,finalJ).contentEquals("-")){
                            weekly_cal.set_calender_data(finalI,finalJ,"+");
                            cell.setBackgroundColor(Color.parseColor("#006400"));
                        }else{
                            weekly_cal.set_calender_data(finalI,finalJ,"-");
                            String color = "#24DF5C";
                            if (finalI%2==0) color = "#ffffff";
                            cell.setBackgroundColor(Color.parseColor(color));
                        }

                    }
                });
                row.addView(cell);
            }
            row.setBackgroundColor(Color.parseColor(color));
            scrollablePart.addView(row);
            time = time.plus(30, ChronoUnit.MINUTES);
        }
    }

    public TextView makeTableRowWithText(String text, int widthInPercentOfScreenWidth, int fixedHeightInPixels) {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        recyclableTextView = new TextView(getContext());
        recyclableTextView.setText(text);
        recyclableTextView.setTextColor(Color.BLACK);
        recyclableTextView.setTextSize(12);
        recyclableTextView.setGravity(Gravity.CENTER);
        recyclableTextView.setWidth(widthInPercentOfScreenWidth * screenWidth / 100);
        recyclableTextView.setHeight(fixedHeightInPixels);
        return recyclableTextView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Calender");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.calen, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                create_popup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void create_popup(){
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.pop_up, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);

        Spinner spinner = (Spinner) popupView.findViewById(R.id.assign_activity);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, user_activities.loaded_activities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                activity_to_add = (String) parent.getItemAtPosition(pos);
                Log.d("Activity add",activity_to_add);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button schedule = popupView.findViewById(R.id.schedule_button);
        schedule.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View view) {
                Log.d("Button Click",activity_to_add);
                for(int I=0;I<weekly_cal.getNO_ROWS();I++){
                    for(int J=0;J<7;J++){
                        if(weekly_cal.get_calendar_data(I,J).contentEquals("+")){
                            weekly_cal.set_calender_data(I,J,activity_to_add);
                        }
                    }
                }
                weekly_cal.save_Calendar();
                refresh_frag();
                popupWindow.dismiss();
            }
        });
    }

    public boolean check_leap(int year){
        if (year % 400 ==0 ) return true;
        if (year % 100 == 0) return false;
        if (year % 4 == 0) return true;
        return false;
    }

    public void create_date_pop_up(){
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.date_pop_up, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);

        final String[] selected_day  = new String[1];
        final String[] selected_month = new String[1];
        final String[] selected_year = new String[1];


        NumberPicker year =  popupView.findViewById(R.id.year);
        NumberPicker month =  popupView.findViewById(R.id.month);
        final NumberPicker day = popupView.findViewById(R.id.day);
        Button set = popupView.findViewById(R.id.date_set_button);

        year.setMinValue(2019);
        year.setMaxValue(2069);
        month.setMinValue(1);
        month.setMaxValue(12);
        day.setMinValue(1);
        day.setMaxValue(31);
        selected_year[0] = "2019";
        selected_day[0] = "01";
        selected_month[0] = "01";

        year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
               selected_year[0] = Integer.toString(newVal);
            }
        });


        month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                selected_month[0] = Integer.toString(newVal);
                if(selected_month[0].length()==1){
                    selected_month[0] = "0"+selected_month[0];
                }
                int[] long_months =  {1,3,5,7,8,10,12};
                boolean day_fixed = false;
                for(int I=0; I<long_months.length;I++){
                    if(newVal==long_months[I]){
                        day.setMaxValue(31);
                        day_fixed = true;
                    }
                }
                if(!day_fixed){
                    if(newVal == 2){
                        if(check_leap(Integer.parseInt(selected_year[0]))){
                            day.setMaxValue(29);
                        }else{
                            day.setMaxValue(28);
                        }
                    }else{
                        day.setMaxValue(30);
                    }
                }
            }
        });

        day.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                selected_day[0] = Integer.toString(newVal);

                if(selected_day[0].length()==1){
                    selected_day[0] = "0"+selected_day[0];
                }
            }
        });

        set.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View view) {
                date = selected_year[0]+"-"+selected_month[0]+"-"+selected_day[0];
                refresh_frag();
                popupWindow.dismiss();
            }
        });
    }


    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        if(interceptScroll){
            interceptScroll=false;
            if(scrollView == scrollView1) {
                scrollView2.onOverScrolled(x,y,true,true);
            } else if(scrollView == scrollView2) {
                scrollView1.onOverScrolled(x,y,true,true);
            }
            interceptScroll=true;
        }
    }

}
