package com.example.ea_reminder;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;

public class ForceVibrate extends Service{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Vibrator vibr = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        long [] vib = {50,500,50,500,50,1000};
        vibr.vibrate(vib, -1);
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }
}
/*
public class ForceVibrate extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vibrator vibr = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        long [] vib = {50,500,50,500,50,1000};
        vibr.vibrate(vib, -1);
        finish();
    }
}*/
