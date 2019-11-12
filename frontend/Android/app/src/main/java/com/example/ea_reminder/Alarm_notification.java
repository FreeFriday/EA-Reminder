package com.example.ea_reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.SymbolTable;
import android.os.Vibrator;

public class Alarm_notification extends BroadcastReceiver {
    final String apppack = "kr.ac.snu.mobile";
    String appname="";
    long [] vib = {50,500,50,500,50,1000};
    @Override
    public void onReceive(Context context, Intent intent) {
        String cname = intent.getStringExtra("Classname");
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        PackageManager pm = context.getPackageManager();
        Intent launchintent=null;
        try{
            if(pm!=null){
                ApplicationInfo appinfo = context.getPackageManager().getApplicationInfo(apppack,0);
                appname = (String)pm.getApplicationLabel(appinfo);
                launchintent = pm.getLaunchIntentForPackage(apppack);
            }
        } catch (PackageManager.NameNotFoundException e) {
            System.out.println("MySNU APP not found");
        }
        System.out.println("Carry On Alarming");
        Notification.Builder nb = new Notification.Builder(context);
        Intent inten = launchintent;
        nb.setSmallIcon(R.drawable.btn_check).setTicker(cname+" 수업이 곧 시작됩니다. 출석하세요!").setWhen(System.currentTimeMillis()).setContentTitle(cname+" 강좌 출결 알림")
                .setContentText("수업이 곧 시작됩니다. 알림을 눌러 전자출결 하세요.").setAutoCancel(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String chname = "Tester";
            NotificationChannel nc = new NotificationChannel(chname,"Readable Tester Title",NotificationManager.IMPORTANCE_HIGH);
            nc.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            nc.setLightColor(Color.GREEN);
            nc.setVibrationPattern(vib);
            nc.enableVibration(true);
            nc.enableLights(true);
            nm.createNotificationChannel(nc);
            nb.setChannelId(chname);
        }

        System.out.println("Notification.Builder="+nb);
        if(launchintent!=null){
            PendingIntent pintent =PendingIntent.getActivity(context, 0, inten, 0);
            nb.setContentIntent(pintent);
        }
        nm.notify(1,nb.build());

        SharedPreferences sp = context.getSharedPreferences(MainActivity.prefname,Context.MODE_PRIVATE);
        boolean forcevibing = true;
        if(sp.contains(MainBoard.pref_forcevib))forcevibing=sp.getBoolean(MainBoard.pref_forcevib,true);

        if(forcevibing) {
            Intent newintent = new Intent(context, ForceVibrate.class);
            PendingIntent newpi = PendingIntent.getActivity(context, -1, newintent, PendingIntent.FLAG_ONE_SHOT);
            try {
                newpi.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }
}
