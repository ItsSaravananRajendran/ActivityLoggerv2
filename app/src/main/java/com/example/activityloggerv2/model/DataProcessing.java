package com.example.activityloggerv2.model;

import android.os.Build;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class DataProcessing {

    private File log_file = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS), "activities_log.csv");
    private ArrayList<HashMap<String, LogData>> quadrant_data = new ArrayList<HashMap<String, LogData>>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DataProcessing() {
        dataSplit(read_log_file(log_file));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<LogData> read_log_file(File log_activity_file) {
        String[] val;
        ArrayList<LogData> data_array = new ArrayList<LogData>();
        try {
            if (!log_activity_file.exists()) {
                log_activity_file.createNewFile();
            } else {
                BufferedReader br = new BufferedReader(new FileReader(log_activity_file));
                String line;
                while ((line = br.readLine()) != null) {
                    val = line.split(",");
                    LogData log = new LogData();
                    log.populate(val);
                    data_array.add(log);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data_array;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void dataSplit(ArrayList<LogData> data_array) {
        for (int I = 0; I < 4; I++) {
            quadrant_data.add(new HashMap<String, LogData>());
        }
        for (LogData data : data_array) {

            int index = 0;
            switch (data.getType()) {
                case "Q1":
                    index = 0;
                    break;
                case "Q2":
                    index = 1;
                    break;
                case "Q3":
                    index = 2;
                    break;
                case "Q4":
                    index = 3;
                    break;
            }
            HashMap<String, LogData> temp = quadrant_data.get(index);
            if (temp.containsKey(data.getCurrent())) {
                LogData ld = temp.get(data.getCurrent());
                ld.setSpent(ld.getSpent() + data.getSpent());
                temp.put(ld.getCurrent(), ld);
            } else {
                temp.put(data.getCurrent(), data);
            }
            quadrant_data.set(index, temp);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public float[] percentage_quadrant() {
        float totalTime = 0.0f;
        float[] result = {0, 0, 0, 0, 0};
        for (int I = 0; I < 4; I++) {
            Iterator<Map.Entry<String, LogData>> iter = quadrant_data.get(I).entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, LogData> data = iter.next();
                result[I] += data.getValue().getSpent();
                totalTime += data.getValue().getSpent();
            }
        }
        for (int I = 0; I < 4; I++) {
            result[I] /= totalTime;
        }
        result[4] = totalTime;
        return result;
    }

    public HashMap<String, LogData> get_activities(String type) {
        switch (type) {
            case "Q1":
                return quadrant_data.get(0);
            case "Q2":
                return quadrant_data.get(1);
            case "Q3":
                return quadrant_data.get(2);
            case "Q4":
                return quadrant_data.get(3);

        }
        return new HashMap<String, LogData>();
    }
}
