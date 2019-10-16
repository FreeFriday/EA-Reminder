package com.example.ea_reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainSetting extends AppCompatActivity {
    Button b_erzinfo; //서버의 로그인 정보 삭제
    Button b_refresh; //서버에서 시간표 다시 불러오기
    Button b_boardset; //수동으로 시간표 설정

    boolean refreshed=false;
    boolean madeintent = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setting);

        b_erzinfo = findViewById(R.id.b_erzinfo);
        b_refresh = findViewById(R.id.b_refresh);
        b_boardset = findViewById(R.id.b_boardset);

        b_erzinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: 서버에 로그인 정보 삭제 요청
                SharedPreferences sp = getSharedPreferences(MainActivity.prefname,MODE_PRIVATE);
                SharedPreferences.Editor sped = sp.edit();
                sped.remove("Logined");
                sped.commit();
                Toast.makeText(getApplicationContext(),"로그인 정보가 삭제되었습니다.\n앱 재시작시 로그인해주세요.",Toast.LENGTH_LONG).show();
            }
        });

        b_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: 서버에서 시간표 정보 다시 요청
                BoardSetting.DelDB(getApplicationContext());

                refreshed=true;
                makeintent(refreshed);
                Toast.makeText(getApplicationContext(),"시간표가 초기화되었습니다.",Toast.LENGTH_LONG).show();
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
