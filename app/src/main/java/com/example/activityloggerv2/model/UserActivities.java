package com.example.activityloggerv2.model;


import android.arch.lifecycle.ViewModel;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class UserActivities extends ViewModel {

    private File file = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS), "activites.csv");
    private File log_file = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS), "activities_log.csv");
    private File current_activity = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS), "current_activity.csv");
    private String current = "";
    private String currentType = "";
    private HashMap<String, String> activity_types = new HashMap<String, String>();
    private Instant start;
    public List<String> loaded_activities = new ArrayList<String>();


    public UserActivities() {
        load_current();
        load_activities();
    }

    private void write_file(File file, String writable, Boolean append) {
        try {
            OutputStreamWriter file_writer = new OutputStreamWriter(new FileOutputStream(file, append));
            BufferedWriter buffered_writer = new BufferedWriter(file_writer);
            buffered_writer.write(writable);
            buffered_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log_activity() {
        if (current != "") {
            String start_time = start.toString();
            int min = duration();
            String end = java.time.Instant.now().toString();
            String writable = current + "," + currentType + "," + start_time + "," + end + "," + Integer.toString(min) + "\n";
            write_file(log_file, writable, true);
        }

    }

    public void load_current() {
        try {
            if (!current_activity.exists()) {
                current_activity.createNewFile();
            } else {
                BufferedReader br = new BufferedReader(new FileReader(current_activity));
                String line = br.readLine();
                if (line != null && line.split(",").length == 3) {
                    current = line.split(",")[0];
                    currentType = line.split(",")[1];
                    start = Instant.parse(line.split(",")[2]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load_activities() {
        loaded_activities.add(current);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String[] val;
            while ((line = br.readLine()) != null) {
                val = line.split(",");
                activity_types.put(val[0], val[1]);
                if (!val[0].contentEquals(current))
                    loaded_activities.add(val[0]);
                else{
                    currentType = activity_types.get(current);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save_activities(String message) {
        write_file(file, message + "\n", true);
    }

    public int duration() {
        Instant time_now = java.time.Instant.now().plusSeconds(330 * 60);
        if (start == null)
            start = java.time.Instant.now().plusSeconds(330 * 60);
        return (int) Duration.between(start, time_now).toMinutes();

    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String select) {
        current = select;
        currentType = activity_types.get(current);
        start = java.time.Instant.now().plusSeconds(330 * 60);
        write_file(current_activity, current + "," + currentType + "," + start.toString(), false);
    }

    public void refresh(){
        loaded_activities = new ArrayList<String>();
        activity_types = new HashMap<String, String>();
        load_current();
        load_activities();
    }
}

