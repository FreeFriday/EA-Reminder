package com.example.ea_reminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BoardSetting extends AppCompatActivity {
    EditText bsadd_name;//추가할 강좌 이름
    EditText bsadd_time;//추가할 강좌 시간
    ImageButton b_add;//강좌 추가 버튼
    ImageButton b_deleteall;//강좌 일괄 삭제

    RecyclerView rv;

    ArrayList<RecyclerBoardSetting> rbslist; //강좌 정보가 있는 리스트
    RecyclerBoardSettingAdapter rbsa;

    boolean res=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_setting);

        bsadd_name = findViewById(R.id.bsadd_name);
        bsadd_time = findViewById(R.id.bsadd_time);
        b_add = findViewById(R.id.b_add);
        b_deleteall = findViewById(R.id.b_deleteall);

        rv = findViewById(R.id.rv2);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rbslist = new ArrayList<RecyclerBoardSetting>();
        rbsa = new RecyclerBoardSettingAdapter(rbslist,getApplicationContext(),this);
        rv.setAdapter(rbsa);

        //TODO: 키보드 팝업 시 RecyclerView 높이 줄이기

        //강좌 추가 버튼
        b_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tempname = bsadd_name.getText().toString();
                String temptime = bsadd_time.getText().toString();
                ArrayList<Integer> dates = new ArrayList<Integer>();
                ArrayList<Integer> times = new ArrayList<Integer>();//HHMM 형식
                if(CheckNameAndTime.CheckNameAndTime(tempname,temptime,dates,times,getApplicationContext())){//형식에 맞는지 확인
                    long tempid= Add2DB(getApplicationContext(),tempname,TimeStringMaker(dates,times),"1:30",true,false,0);
                    RecyclerBoardSetting temprb = new RecyclerBoardSetting(tempid,tempname,TimeStringMaker(dates,times),"1:30",true,false,0);
                    rbslist.add(temprb);
                    //rbsa.notifyDataSetChanged();
                    rbsa.alertadapter();
                    bsadd_name.setText("");
                    bsadd_time.setText("");

                    if(bsadd_name.hasFocus())bsadd_name.clearFocus();
                    if(bsadd_time.hasFocus())bsadd_time.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        //TODO: 일괄삭제 버튼 구현
        /*
        //Test case:
        for(int i=0;i<3;i++){
            RecyclerBoardSetting temprb = new RecyclerBoardSetting("이산수학","월 11:00, 수 11:00");
            rbslist.add(temprb);
            rbsa.notifyDataSetChanged();
        }*/
        getDB(this);
    }
    public void getDB(Context context){
        //TODO: static 함수로 바꾸기
        rbslist.clear();
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
            RecyclerBoardSetting temprb = new RecyclerBoardSetting(tempid,tempclassname,tempstart,templength,tempeaon,tempalarm,temptimer);
            rbslist.add(temprb);
            rbsa.notifyDataSetChanged();
        }
    }
    public static String TimeStringMaker(ArrayList<Integer> dates, ArrayList<Integer> times){
        String returner = "";
        for(int i=0;i<dates.size();i++){
            switch (dates.get(i)){
                case CheckNameAndTime.MON:
                    returner += "월 ";
                    break;
                case CheckNameAndTime.TUE:
                    returner += "화 ";
                    break;
                case CheckNameAndTime.WED:
                    returner += "수 ";
                    break;
                case CheckNameAndTime.THU:
                    returner += "목 ";
                    break;
                case CheckNameAndTime.FRI:
                    returner += "금 ";
                    break;
                case CheckNameAndTime.SAT:
                    returner += "토 ";
                    break;
                case CheckNameAndTime.SUN:
                    returner += "일 ";
                    break;
            }
            returner += (String.format("%02d",times.get(i)/100)+":"+String.format("%02d",times.get(i)%100));
            if(i<dates.size()-1){
                returner +=", ";
            }
        }

        return returner;
    }

    public static long Add2DB(Context context,String classname,String start, String length, boolean eaon, boolean alarm,int timer){
        BoardDBOpenHelper bdboh = new BoardDBOpenHelper(context);
        bdboh.open();
        bdboh.create();
        return bdboh.insertcolumn(classname,start,length,eaon,alarm,timer);
    }
    public static void DelDB(Context context){
        BoardDBOpenHelper bdboh = new BoardDBOpenHelper(context);
        bdboh.open();
        bdboh.create();
        Cursor cursor = bdboh.selectcol();
        while(cursor.moveToNext()){
            long nowid = cursor.getLong(0);
            Alarmer.deletealarm(context,(int)nowid);
        }
        bdboh.DelDB();
    }
    public void setres(){
        System.out.println("Called setres()");
        if(res==false){
            res=true;
            Intent resint = new Intent();
            resint.putExtra("changed",true);
            setResult(RESULT_OK,resint);
            System.out.println("Made intent result = "+res);
        }
    }
}
