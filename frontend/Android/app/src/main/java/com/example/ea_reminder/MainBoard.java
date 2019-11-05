package com.example.ea_reminder;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainBoard extends AppCompatActivity {
    ImageButton b_setting; //설정 메뉴 진입 버튼
    Switch sw_applyall; //알림 일괄적용 스위치
    TextView tx_applyall; //일괄적용 텍스트
    Button b_time_all; //알림 타이머 일괄적용 버튼

    RecyclerView rv; //강좌 정보 담길 RecyclerView

    ArrayList<RecyclerBoard> rblist; //강좌 정보가 있는 리스트
    RecyclerBoardAdapter rba; //RecyclerViewAdapter

    boolean allswitch = true;//일괄적용 스위치 값
    int alltimer = 0;//알림 시간 일괄적용 값

    SharedPreferences sp; //아이디, 비번, 로그인 전적 저장 Preference
    SharedPreferences.Editor sped;

    Context context;

    public static final String pref_allsw = "allsw";
    public static final String pref_allbt = "allbw";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_board);
        context = this;
        sp = getSharedPreferences(MainActivity.prefname,MODE_PRIVATE);
        sped = sp.edit();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        if(sp.contains(pref_allsw)){
            allswitch=sp.getBoolean(pref_allsw,true);
        }
        if(sp.contains(pref_allbt)){
            alltimer = sp.getInt(pref_allbt,0);
        }
        if(sp.getBoolean(MainActivity.pref_logined,false)){

        }
        else{
            Intent intent = getIntent();
            String js = intent.getStringExtra("json");
            String id = intent.getStringExtra("id");
            String pw = intent.getStringExtra("pw");

            try {
                JParser jp = new JParser();
                jp.boardinfoParse(js,name,time);
            } catch (ParseException e) {
                Toast.makeText(getApplicationContext(),"MySNU 정보 오류입니다.\n다시 로그인 해 주세요.",Toast.LENGTH_LONG).show();
                finish();
            }
            catch (RuntimeException e){
                Toast.makeText(getApplicationContext(),"MySNU 정보가 없습니다.\n아이디 혹은 비밀번호를 확인해주세요.",Toast.LENGTH_LONG).show();
                finish();
            }
            sped.putBoolean(MainActivity.pref_logined, true);
            sped.putString(MainActivity.pref_id,id);
            sped.putString(MainActivity.pref_pw,pw);
            sped.apply();
            for(int i=0;i<name.size();i++){
                BoardSetting.Add2DB(this,name.get(i),time.get(i),"1:30",true,allswitch,alltimer);
            }

        }
        b_setting = (ImageButton)findViewById(R.id.b_setting);

        sw_applyall = (Switch)findViewById(R.id.sw_applyall);
        tx_applyall = (TextView)findViewById(R.id.tx_applyall);
        b_time_all = (Button)findViewById(R.id.b_time_all);

        //설정 메뉴 진입 버튼
        b_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2setting = new Intent(getApplicationContext(), MainSetting.class);
                //startActivity(intent2setting);
                startActivityForResult(intent2setting,1000);
            }
        });

        rv = (RecyclerView)findViewById(R.id.rv);

        //RecyclerView 설정 및 어뎁터 장착
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rblist = new ArrayList<RecyclerBoard>();
        rba = new RecyclerBoardAdapter(rblist,this);
        rv.setAdapter(rba);

        //일괄 적용 스위치 설정
        sw_applyall.setChecked(allswitch);
        RecyclerBoardAdapter.swOnClickListener.setTimerButton(sw_applyall,b_time_all);
        RecyclerBoardAdapter.btOnClickListener.setTimer(b_time_all,alltimer);

        sw_applyall.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                allswitch = ((Switch)view).isChecked();
                sped.putBoolean(pref_allsw,allswitch);
                sped.apply();
                RecyclerBoardAdapter.swOnClickListener.setTimerButton((Switch)view,b_time_all);
                for(int i=0;i<rba.swlist.size();i++){
                    rba.swlist.get(i).setChecked(allswitch);
                    RecyclerBoardAdapter.swOnClickListener.setTimerButton(rba.swlist.get(i),rba.btlist.get(i));
                }
                allchangesw acsw = new allchangesw();
                acsw.execute();
                Toast.makeText(context,"처리 중입니다.",Toast.LENGTH_SHORT).show();
            }
        });
        //알림 시간 일괄 적용 설정
        b_time_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alltimer=RecyclerBoardAdapter.btOnClickListener.incTimer(alltimer);
                sped.putInt(pref_allbt,alltimer);
                sped.apply();
                RecyclerBoardAdapter.btOnClickListener.setTimer(b_time_all,alltimer);
                for(int i=0;i<rba.btlist.size();i++){
                    RecyclerBoardAdapter.btOnClickListener.setTimer(rba.btlist.get(i), alltimer);
                }
                allchangebt acbt = new allchangebt();
                acbt.execute();
                Toast.makeText(context,"처리 중입니다.",Toast.LENGTH_SHORT).show();
            }
        });
        //getDB(this);

        rblist.clear();
        BoardDBOpenHelper bdboh = new BoardDBOpenHelper(this);
        bdboh=bdboh.open();
        bdboh.create();

        Cursor cursor = bdboh.selectcol();
        while(cursor.moveToNext()){
            long tempid = cursor.getLong(0);
            String tempclassname = cursor.getString(1);
            String tempstart = cursor.getString(2);
            String templength = cursor.getString(3);
            boolean tempeaon = cursor.getInt(4)>0; //EAON 타입 정수형이어야 할수도
            boolean tempalarm = cursor.getInt(5)>0; //마찬가지
            int temptimer = cursor.getInt(6);
            RecyclerBoard temprb = new RecyclerBoard(tempclassname,tempstart,tempalarm,temptimer,tempid);
            rblist.add(temprb);
            rba.notifyDataSetChanged();
        }
        bdboh.close();
        /*
        {
            RecyclerBoard testrb = rblist.get(0);
            setalarm(testrb.name, testrb.time, testrb.timer,0);
            System.out.println("Set alarm for 0");

        }
        {
            RecyclerBoard testrb = rblist.get(1);
            setalarm(testrb.name, testrb.time, testrb.timer,1);
            System.out.println("Set alarm for 1");
        }*/
        /*
        for(int i=0;i<name.size();i++){
            RecyclerBoard temprb = new RecyclerBoard(name.get(i),time.get(i),true,0,0);
            rblist.add(temprb);
            rba.notifyDataSetChanged();
        }*/

    }
    public void getDB(Context context){
        rblist.clear();
        BoardDBOpenHelper bdboh = new BoardDBOpenHelper(context);
        bdboh=bdboh.open();
        bdboh.create();

        Cursor cursor = bdboh.selectcol();
        while(cursor.moveToNext()){
            long tempid = cursor.getLong(0);
            String tempclassname = cursor.getString(1);
            String tempstart = cursor.getString(2);
            String templength = cursor.getString(3);
            boolean tempeaon = cursor.getInt(4)>0; //EAON 타입 정수형이어야 할수도
            boolean tempalarm = cursor.getInt(5)>0; //마찬가지
            int temptimer = cursor.getInt(6);
            RecyclerBoard temprb = new RecyclerBoard(tempclassname,tempstart,tempalarm,temptimer,tempid);
            rblist.add(temprb);
        }
        rba.notifyDataSetChanged();
        bdboh.close();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setalarm(String name, String time,int offset,int requestcode,Context context){
        String day="";
        int h=0;
        int m=0;
        StringTokenizer str = new StringTokenizer(time," ");
        day = str.nextToken();
        String temptime = str.nextToken();
        str = new StringTokenizer(temptime,":");
        h = Integer.parseInt(str.nextToken());
        m = Integer.parseInt(str.nextToken());
        int realoffset=0;
        switch (offset){
            case RecyclerBoard.TIMER_10MIN:
                realoffset=10;
                break;
            case RecyclerBoard.TIMER_5MIN:
                realoffset=5;
                break;
            case RecyclerBoard.TIMER_1MIN:
                realoffset=1;
                break;
        }
        m-=realoffset;
        if(m<0){
            m+=60;
            h--;
        }
        System.out.println("day="+day+",h="+Integer.toString(h)+",m="+Integer.toString(m));
        Alarmer.setalarm(context,day,h,m,name,requestcode);
    }
    public static void delalarm(int requestcode,Context context){
        Alarmer.deletealarm(context,requestcode);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case 1000:
                    System.out.println("Mainboard: Got result 1000");
                    System.out.println("Extradata = "+data.getBooleanExtra("boardchange",false));
                    if(data.getBooleanExtra("boardchange",false)){
                        getDB(getApplicationContext());
                    }

                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        finishAffinity();
        super.onDestroy();
    }
    public class allchangesw extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            BoardDBOpenHelper bdboh = new BoardDBOpenHelper(context);
            bdboh=bdboh.open();
            bdboh.create();
            for(int i=0;i<rba.swlist.size();i++){
                if(rblist.get(i)!=null){
                    boolean tempon = rblist.get(i).on;
                    rblist.get(i).on=allswitch;

                    if(tempon!=allswitch){
                        RecyclerBoard rb = rblist.get(i);
                        bdboh.updatecol(rb.id,rb.name,rb.time,"1:30",true,rb.on,rb.timer);
                        if(rb.on)setalarm(rb.name,rb.time,rb.timer,(int)rb.id,context);
                        else delalarm((int)rb.id,context);
                    }
                }
            }
            bdboh.close();
            return null;
        }
        protected void onPostExecute(Void v){
            Toast.makeText(context,"전체 강좌의 알람이 "+(allswitch?"켜졌습니다.":"꺼졌습니다."),Toast.LENGTH_LONG).show();
        }
    }
    public class allchangebt extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            BoardDBOpenHelper bdboh = new BoardDBOpenHelper(context);
            bdboh=bdboh.open();
            bdboh.create();
            for(int i=0;i<rba.btlist.size();i++){
                int temptimer = rblist.get(i).timer;
                rblist.get(i).timer=alltimer;

                if(temptimer!=alltimer){
                    RecyclerBoard rb = rblist.get(i);
                    bdboh.updatecol(rb.id,rb.name,rb.time,"1:30",true,rb.on,rb.timer);
                    if(rb.on)setalarm(rb.name,rb.time,rb.timer,(int)rb.id,context);
                    else delalarm((int)rb.id,context);
                }
            }
            bdboh.close();
            return null;
        }
        protected void onPostExecute(Void v){
            Toast.makeText(context,"전체 강좌의 알림이 "+RecyclerBoardAdapter.returntimestring(alltimer)+"으로 설정되었습니다",Toast.LENGTH_LONG).show();
        }
    }

}
