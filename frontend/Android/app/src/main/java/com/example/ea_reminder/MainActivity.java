package com.example.ea_reminder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView tx_warning; //아이디랑 비번은 암호화 된다는 경고문
    EditText t_id; //아이디 입력창
    EditText t_pw; //비번 입력창
    Button b_login; //로그인 버튼

    ProgressBar pbar;

    SharedPreferences sp; //아이디, 비번, 로그인 전적 저장 Preference
    public final String requrl = "http://snu.axiss.xyz/api/table/";

    public static final String prefname = "LoginPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tx_warning = (TextView) findViewById(R.id.tx_warning);
        t_id = (EditText)findViewById(R.id.t_id);
        t_pw = (EditText)findViewById(R.id.t_pw);
        b_login = (Button)findViewById(R.id.b_login);
        pbar = (ProgressBar)findViewById(R.id.pBar);

        sp = getSharedPreferences(prefname,MODE_PRIVATE);
        //System.out.println("Logined pref = "+sp.getBoolean("Logined",false));




        if(sp.contains("Logined")){
            go2board(null,null,null);
            //TODO: 이미 로그인한 전적이 있다면 바로 시간표 창으로 이동
        }
        else{
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setTitle("절전 모드 해제");
            ad.setMessage("절전 모드는 앱의 알림 기능에 영향을 줄 수 있습니다.\n하단의 확인을 누르면 설정창으로 이동합니다.");
            ad.setButton(AlertDialog.BUTTON_NEUTRAL, "확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    String packageName = getPackageName();
                    PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);

                    if (pm.isIgnoringBatteryOptimizations(packageName))
                        intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    else {
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + packageName));
                    }
                    startActivity(intent);
                }
            });
            ad.show();
        }

        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                sp.edit().putString("MyID", t_id.getText().toString()); // 아이디 Preference에 저장
                sp.edit().putString("MyPW", t_pw.getText().toString()); // 비번 Preference에 저장
                */

                //TODO: 서버에 로그인 요청 보내기
                ContentValues values = new ContentValues();
                values.put("id",t_id.getText().toString());
                values.put("pw",t_pw.getText().toString());
                pbar.setVisibility(View.VISIBLE);
                b_login.setVisibility(View.INVISIBLE);
                logintask nt = new logintask(requrl,values,pbar,true);
                nt.execute();
                //TODO: [의견 필요]: 서버에 알림 설정 내용까지 저장?
                //TODO: 결과 받으면 Prefrence 의 <Logined>값 True로 바꾸기
                //TODO: 결과 가지고 시간표 창으로 이동
                //go2board();
            }
        });
    }
    public class logintask extends AsyncTask<Void,Void,JSONObject>{
        String url;
        ContentValues values;
        View pbar;
        boolean ismain=true;
        public logintask(String url, ContentValues values,View pbar,boolean ismain){
            this.url=url;
            this.values=values;
            this.pbar=pbar;
            this.ismain=ismain;
        }
        @Override
        protected JSONObject doInBackground(Void... voids) {
            HttpRequester hr = new HttpRequester();
            System.out.println("Requested");
            JSONObject res = hr.request(url,values);
            return res;
        }
        @Override
        protected void onPostExecute(JSONObject jsobj){
            if(pbar!=null)pbar.setVisibility(View.INVISIBLE);
            if(jsobj!=null){
                System.out.println(jsobj);
                if(ismain)go2board(jsobj.toString(),t_id.getText().toString(),t_pw.getText().toString());
            }
            else{
                b_login.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"로그인 실패.\n아이디와 비밀번호을 확인해주세요.",Toast.LENGTH_LONG).show();
            }
        }
    }
    void go2board (String res, String id, String pw){
        Intent intent2board = new Intent(getApplicationContext(), MainBoard.class);
        /*
        for(int i=0;i<classinfo.length;i++){
            intent2board.putextra
        }*/
        intent2board.putExtra("json",res);
        intent2board.putExtra("id",id);
        intent2board.putExtra("pw",pw);
        startActivityForResult(intent2board,6000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case 6000:
                    System.out.println("At Mainactivity: Result catched 6000:");
                    finish();
                    break;
            }
        }
    }
}
