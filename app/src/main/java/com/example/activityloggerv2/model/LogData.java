package com.example.activityloggerv2.model;

import android.os.Build;

import java.time.Instant;

import androidx.annotation.RequiresApi;

public class LogData {
    private String current, type;
    private Instant start, end;
    private int spent;

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getSpent() {
        return spent;
    }

    public void setSpent(int spent) {
        this.spent = spent;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void populate(String[] val) {
        current = val[0];
        type = val[1];
        start = Instant.parse(val[2]);
        end = Instant.parse(val[3]);
        spent = Integer.parseInt(val[4]);
    }

    public String toString() {
        return (current + ","
                + type + ","
                + start.toString() + ","
                + end.toString() + ","
                + Integer.toString(spent) + "\n");
    }
}
