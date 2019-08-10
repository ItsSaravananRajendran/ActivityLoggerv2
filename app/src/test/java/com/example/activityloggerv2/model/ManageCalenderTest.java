package com.example.activityloggerv2.model;



import android.support.v4.util.Pair;

import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ManageCalenderTest {

    String date;
    Instant week_start;

    @Before
    public void setUp() throws Exception {
        date = "2019-07-18";
        LocalDate localDate = LocalDate.parse(date); //2017-06-22
        LocalDateTime localDateTime = localDate.atStartOfDay();
        week_start = localDateTime.toInstant(ZoneOffset.ofHoursMinutes(0,0));
    }

    @Test
    public void test_instant_to_rc(){
        ManageCalender testClass = new ManageCalender(date);
        Instant start = week_start.plus(6*30, ChronoUnit.MINUTES);
        Instant end = start.plus(3*30,ChronoUnit.MINUTES);
        int[] res = new int[]{6,0,3};
        assertArrayEquals(res,testClass.instant_to_rc(week_start,start,end) );
    }

    @Test
    public  void test_populate_calender(){
        ManageCalender testClass = new ManageCalender(date);
        Map test_map = new HashMap<Instant, Pair<String,Instant>>();
        Instant start = week_start.plus(15*30,ChronoUnit.MINUTES);
        Instant end = start.plus(3*30,ChronoUnit.MINUTES);
        Pair v= new Pair("Testing",end);
        test_map.put(start,v);
        String[][] test_arr = new String[testClass.getNO_ROWS()][7];
        for(int I=0;I<testClass.getNO_ROWS();I++){
            for(int J=0;J<7;J++){
                test_arr[I][J] = "-";
            }
        }
        for(int I=15;I<18;I++){
            test_arr[I][0] = "Testing";
        }
        testClass.populate_calender(test_map);
        for(int I=0;I<testClass.getNO_ROWS();I++){
            for(int J=0;J<7;J++){
                assertEquals(test_arr[I][J],testClass.get_calendar_data(I,J));
            }
        }


        testClass = new ManageCalender(date);
        test_map = new HashMap<Instant, Pair<String,Instant>>();
        start = week_start.plus(0*30,ChronoUnit.MINUTES);
        end = start.plus(3*30,ChronoUnit.MINUTES);
        v= new Pair("Testing",end);
        test_map.put(start,v);
        test_arr = new String[testClass.getNO_ROWS()][7];
        for(int I=0;I<testClass.getNO_ROWS();I++){
            for(int J=0;J<7;J++){
                test_arr[I][J] = "-";
            }
        }
        for(int I=0;I<3;I++){
            test_arr[I][0] = "Testing";
        }
        testClass.populate_calender(test_map);
        for(int I=0;I<testClass.getNO_ROWS();I++){
            for(int J=0;J<7;J++){
                assertEquals(test_arr[I][J],testClass.get_calendar_data(I,J));
            }
        }
    }

    @Test
    public void test_calender_data_to_string(){

        ManageCalender testClass = new ManageCalender(date);
        Map test_map = new HashMap<Instant, Pair<String,Instant>>();
        Instant start = week_start.plus(15*30,ChronoUnit.MINUTES);
        Instant end = start.plus(3*30,ChronoUnit.MINUTES);
        test_map.put(start,new Pair("Testing",end));
        testClass.populate_calender(test_map);
        String res = "Testing,"+start.toString()+","+end.toString()+"\n";
        assertEquals(res,testClass.calendar_Data_to_string());


        testClass = new ManageCalender(date);
        test_map = new HashMap<Instant, Pair<String,Instant>>();
        start = week_start.plus(0*30,ChronoUnit.MINUTES);
        end = start.plus(3*30,ChronoUnit.MINUTES);
        test_map.put(start,new Pair("Testing",end));
        testClass.populate_calender(test_map);
        res = "Testing,"+start.toString()+","+end.toString()+"\n";
        assertEquals(res,testClass.calendar_Data_to_string());
    }

}