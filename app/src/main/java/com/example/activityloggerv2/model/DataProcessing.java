package com.example.activityloggerv2.model;

import android.os.Build;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;

public class DataProcessing {

    private File log_file = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS), "activities_log.csv");
    private List<LogData> data_array = new ArrayList<LogData>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DataProcessing() {
        read_log_file(log_file);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void read_log_file(File log_activity_file) {
        String[] val;
        try {
            if (!log_activity_file.exists()) {
                log_activity_file.createNewFile();
            } else {
                BufferedReader br = new BufferedReader(new FileReader(log_activity_file));
                String line = br.readLine();
                if (line != null) {
                    val = line.split(",");
                    LogData log = new LogData();
                    log.populate(val);
                    data_array.add(log);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public float[] percentage_quadrant() {
        float totalTime = 0;
        float[] result = {0, 0, 0, 0, 0};
        int[] quadrantTime = {0, 0, 0, 0};
        for (LogData data : data_array) {
            switch (data.getType()) {
                case "Q1":
                    quadrantTime[0] += data.getSpent();
                    break;
                case "Q2":
                    quadrantTime[1] += data.getSpent();
                    break;
                case "Q3":
                    quadrantTime[2] += data.getSpent();
                    break;
                case "Q4":
                    quadrantTime[3] += data.getSpent();
                    break;
            }
            totalTime += data.getSpent();
        }
        for (int I = 0; I < 4; I++) {
            if (totalTime != 0) {
                result[I] = (quadrantTime[I] / totalTime) * 100;

            }
        }
        result[4] = totalTime;
        return result;
    }

}
