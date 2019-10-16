package com.example.ea_reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import java.util.Calendar;

import androidx.annotation.RequiresApi;

public class Alarmer {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setalarm(Context context, String day, int h, int m, String name, int requestcode){
        AlarmManager alm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm_notification.class);
        intent.setAction("com.example.ea_reminder.alarming");
        intent.putExtra("Classname",name);
        PendingIntent pintent = PendingIntent.getBroadcast(context,requestcode,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar cal = Calendar.getInstance();
        int weekday = 0;
        switch (day){
            case "월":
                weekday=Calendar.MONDAY;
                break;
            case "화":
                weekday=Calendar.TUESDAY;
                break;
            case "수":
                weekday=Calendar.WEDNESDAY;
                break;
            case "목":
                weekday=Calendar.THURSDAY;
                break;
            case "금":
                weekday=Calendar.FRIDAY;
                break;
            case "토":
                weekday=Calendar.SATURDAY;
                break;
            case "일":
                weekday=Calendar.SUNDAY;
                break;
        }
        cal.get(Calendar.WEEK_OF_YEAR);
        Calendar tempcal = Calendar.getInstance();
        tempcal.setWeekDate(cal.getWeekYear(),cal.get(Calendar.WEEK_OF_YEAR),weekday);
        tempcal.set(Calendar.HOUR_OF_DAY,h);
        tempcal.set(Calendar.MINUTE,m);
        tempcal.set(Calendar.SECOND,0);
        tempcal.set(Calendar.MILLISECOND,0);
        if(cal.compareTo(tempcal)>0){
            tempcal.add(Calendar.DAY_OF_YEAR,7);
        }
        alm.setRepeating(AlarmManager.RTC_WAKEUP, tempcal.getTimeInMillis(),7*24*60*60*1000,pintent);

        System.out.println("Alarm Set: Y="+tempcal.get(Calendar.YEAR)+",M="+tempcal.get(Calendar.MONTH)+",D="+tempcal.get(Calendar.DATE)+",H="+tempcal.get(Calendar.HOUR_OF_DAY)+",M="+tempcal.get(Calendar.MINUTE)
                +",S="+tempcal.get(Calendar.SECOND));
    }
    public static void deletealarm(Context context, int requestcode){
        AlarmManager alm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm_notification.class);
        PendingIntent pintent = PendingIntent.getBroadcast(context,requestcode,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        alm.cancel(pintent);
    }
}
