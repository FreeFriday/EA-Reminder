package com.example.ea_reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class MainSetting extends AppCompatActivity {
    Button b_erzinfo; //서버의 로그인 정보 삭제
    Button b_refresh; //서버에서 시간표 다시 불러오기
    Button b_boardset; //수동으로 시간표 설정

    SharedPreferences sp;
    SharedPreferences.Editor sped;

    boolean refreshed=false;
    boolean madeintent = false;

    ProgressBar pbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setting);

        b_erzinfo = findViewById(R.id.b_erzinfo);
        b_refresh = findViewById(R.id.b_refresh);
        b_boardset = findViewById(R.id.b_boardset);

        pbar = findViewById(R.id.pbar2);

        sp = getSharedPreferences(MainActivity.prefname,MODE_PRIVATE);

        b_erzinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그인 정보 삭제
                sped = sp.edit();
                sped.remove(MainActivity.pref_logined);
                sped.remove(MainActivity.pref_id);
                sped.remove(MainActivity.pref_pw);
                sped.remove(MainBoard.pref_allsw);
                sped.remove(MainBoard.pref_allbt);
                sped.commit();
                Toast.makeText(getApplicationContext(),"로그인 정보가 삭제되었습니다.\n앱 재시작시 로그인해주세요.",Toast.LENGTH_LONG).show();
            }
        });

        b_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //시간표 다시 불러오기
                String id = sp.getString(MainActivity.pref_id,"");
                String pw = sp.getString(MainActivity.pref_pw,"");
                System.out.println("id="+id+", pw="+pw);
                if(id==null||pw==null){
                    Toast.makeText(getApplicationContext(),"로그인 정보가 손상되었습니다. \n로그인 정보를 삭제해주세요.",Toast.LENGTH_LONG).show();
                }
                else{
                    refreshed=true;
                    makeintent(refreshed);

                    ContentValues values = new ContentValues();
                    values.put("id", id);
                    values.put("pw",pw);

                    ArrayList<String> name = new ArrayList<>();
                    ArrayList<String> time = new ArrayList<>();

                    pbar.setVisibility(View.VISIBLE);

                    int allbt =0;
                    if(sp.contains(MainBoard.pref_allbt))allbt = sp.getInt(MainBoard.pref_allbt,0);

                    MainActivity.staticlogintask lt = new MainActivity.staticlogintask(MainActivity.requrl,values,pbar,getApplicationContext(),name,time,sp.getBoolean(MainBoard.pref_allsw,true),allbt);
                    lt.execute();
                }
            }
        });

        b_boardset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2boardsetting = new Intent(getApplicationContext(), BoardSetting.class);
                startActivityForResult(intent2boardsetting,2000);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("At Mainsetting onActivityResult called");
        System.out.println("RequestCode = "+requestCode);
        System.out.println("ResultCode = "+resultCode);
        System.out.println("Data = "+data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case 2000:
                    System.out.println("At Mainsetting catched result 2000:");
                    boolean changed = data.getBooleanExtra("changed",false);
                    makeintent(changed);
                    break;
            }
        }
    }
    void makeintent(boolean changed){
        if(!madeintent){
            Intent res = new Intent();
            res.putExtra("boardchange",changed);
            setResult(RESULT_OK,res);
            madeintent=true;
            System.out.println("Made intent: value = "+changed);
        }
    }
}
