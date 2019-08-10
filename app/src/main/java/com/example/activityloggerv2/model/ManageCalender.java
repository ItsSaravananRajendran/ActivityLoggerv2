package com.example.activityloggerv2.model;

import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.support.v4.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


@RequiresApi(api = Build.VERSION_CODES.O)
public class ManageCalender {

    int DIFFMIN = 30;
    int NO_ROWS = 24*60/DIFFMIN;
    String[][] calendar_Data = new String [(NO_ROWS)][7];
    private File user_calender = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS), "calender.csv");
    Instant week_start ;
    public int getNO_ROWS() {
        return NO_ROWS;
    }


    public void set_calender_data(int I,int J,String data){
        calendar_Data[I][J] = data;
    }

    public ManageCalender(String date){
        LocalDate localDate;
        if(date ==""){
            localDate =  LocalDate.now();
            while (localDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                localDate = localDate.minusDays(1);
            }
        }else{
            localDate = LocalDate.parse(date); //2017-06-22
        }
        LocalDateTime localDateTime = localDate.atStartOfDay();
        week_start = localDateTime.toInstant(ZoneOffset.ofHoursMinutes(0,0));
        for(int I=0;I<7;I++){
            for(int J=0;J<NO_ROWS;J++){
                calendar_Data[J][I]= "-";
            }
        }
        Map weekly_activites = readWeek(week_start);
        populate_calender(weekly_activites);

    }

    public String get_calendar_data(int I,int J){
        return calendar_Data[I][J];
    }

    public String[] get_header(){
        String[] res =   {"", "", "", "", "", "", ""};
        Instant curr = week_start;
        for (int I=0;I<7;I++){
            LocalDateTime datetime = LocalDateTime.ofInstant(curr, ZoneOffset.ofHoursMinutes(5,30));
            String formatted = DateTimeFormatter.ofPattern("dd/MM" ).format(datetime);
            res[I] = formatted +"\n" + String.valueOf(datetime.getDayOfWeek());
            curr = curr.plus(1,ChronoUnit.DAYS);
        }
        return res;
    }

    private void write_file(File file,String writable, Boolean append){
        try {
            OutputStreamWriter file_writer = new OutputStreamWriter(new FileOutputStream(file,append));
            BufferedWriter buffered_writer = new BufferedWriter(file_writer);
            buffered_writer.write(writable);
            buffered_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save_Calendar(){
        Instant week_end = week_start.plus(7, ChronoUnit.DAYS);
        String writable = "";

        try{
            if(!user_calender.exists()){
                user_calender.createNewFile();
            }else{
                BufferedReader br = new BufferedReader(new FileReader(user_calender));
                String line;
                while ((line = br.readLine()) != null){
                    if (line != null && line.split(",").length == 3){
                        Instant activity_start = Instant.parse(line.split(",")[1]);
                        if(week_start.compareTo(activity_start) < 0 && week_end.compareTo(activity_start) > 0){
                            Instant activity_end = Instant.parse(line.split(",")[2]);
                            if(week_end.compareTo(activity_end)<0){
                                String[] res= line.split(",");
                                line = res[0]+","+week_end.toString()+","+res[2];
                                Log.d("Writable",line);
                            }
                        }else{
                            writable += line+"\n";
                            Log.d("Writable",writable);
                        }
                    }
                }
                br.close();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        writable += calendar_Data_to_string();
        write_file(user_calender,writable,false);
    }

    public String calendar_Data_to_string(){
        String res = "";
        int J=1;
        String activity = calendar_Data[0][0];
        Instant start = week_start, end ;
        for (int I =0 ; I<7 ; I++){
            do{
                if(!calendar_Data[J%NO_ROWS][I].contentEquals(calendar_Data[(J-1)%NO_ROWS][I]) ){
                    if(calendar_Data[J%NO_ROWS][I].contentEquals("-")){
                        end = week_start.plus(I,ChronoUnit.DAYS).plus((J%NO_ROWS)*DIFFMIN,ChronoUnit.MINUTES);
                        res += activity +"," + start.toString()+","+end.toString()+"\n";
                        activity = "-";
                    }else{
                        if(!calendar_Data[(J-1)%NO_ROWS][I].contentEquals("-")){
                            end = week_start.plus(I,ChronoUnit.DAYS).plus((J%NO_ROWS)*DIFFMIN,ChronoUnit.MINUTES);
                            res += activity +"," + start.toString()+","+end.toString()+"\n";
                        }
                        start = week_start.plus(I,ChronoUnit.DAYS).plus((J%NO_ROWS)*DIFFMIN,ChronoUnit.MINUTES);
                        activity = calendar_Data[J%NO_ROWS][I];
                    }
                }
                J++;
            }while(J%NO_ROWS != 0);
        }
        if (!activity.contentEquals("-")){
            end = week_start.plus(7,ChronoUnit.DAYS);
            res += activity +"," + start.toString()+","+end.toString()+"\n";
        }
        return res;
    }

    public void populate_calender(Map activities){
        Set set= activities.entrySet();
        Iterator itr=set.iterator();
        while(itr.hasNext()){
            Map.Entry entry = (Map.Entry)itr.next();
            Pair <String,Instant> v = (Pair<String, Instant>) entry.getValue();
            int[] start = instant_to_rc(week_start, (Instant) entry.getKey(),v.second);
            int I = start[0];
            int J = start[1];
            for(int slot=0;slot<start[2] && J<7;slot++){
                if (I>=NO_ROWS){
                    I = 0;
                    J++;
                }
                calendar_Data[I][J] = v.first;
                I++;
            }

         }
    }

    public int[] instant_to_rc(Instant reference, Instant start, Instant end){
        Duration d = Duration.between(reference,start);
        int c = (int)d.toDays();
        int r = (int)(d.toMinutes()%1440)/DIFFMIN;
        d = Duration.between(start,end);
        int count= (int) (d.toMinutes()/DIFFMIN);
        return new int[] {r,c,count};
    }

    public Map readWeek(Instant week_start){
        Map weekly_activites = new HashMap<Instant,Pair<String,Instant>>();
        Instant week_end = week_start.plus(24*7, ChronoUnit.HOURS);
        try{
            if(!user_calender.exists()){
                user_calender.createNewFile();
            }else{
                BufferedReader br = new BufferedReader(new FileReader(user_calender));
                String line;
                while ((line = br.readLine()) != null){
                    if (line != null && line.split(",").length == 3){
                        Instant activity_start = Instant.parse(line.split(",")[1]);
                        if(week_start.compareTo(activity_start) <= 0 && week_end.compareTo(activity_start) > 0){
                            Instant activity_end = Instant.parse(line.split(",")[2]);
                            String activity = line.split(",")[0];
                            weekly_activites.put(activity_start,new Pair(activity,activity_end));
                        }
                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return weekly_activites;
    }
}
