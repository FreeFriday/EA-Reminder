package com.example.ea_reminder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
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
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView tx_warning; //아이디랑 비번은 암호화 된다는 경고문
    EditText t_id; //아이디 입력창
    EditText t_pw; //비번 입력창
    Button b_login; //로그인 버튼

    ProgressBar pbar;

    SharedPreferences sp; //아이디, 비번, 로그인 전적 저장 Preference
    SharedPreferences.Editor sped;
    public static final String requrl = "http://snu.axiss.xyz/api/table/";

    public static final String prefname = "LoginPref";
    public static final String pref_logined = "Logined";
    public static final String pref_id = "ID";
    public static final String pref_pw = "PW";

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
        sped = sp.edit();
        //System.out.println("Logined pref = "+sp.getBoolean("Logined",false));




        if(sp.contains(pref_logined)){
            go2board(null,null,null);
        }
        else{
            String packageName = getPackageName();
            PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
            if(!pm.isIgnoringBatteryOptimizations(packageName)){
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setTitle("절전 모드 해제");
                ad.setMessage("절전 모드는 앱의 알림 기능에 영향을 줄 수 있습니다.\n하단의 확인을 눌러 설정 변경을 허용해주세요.");
                ad.setButton(AlertDialog.BUTTON_NEUTRAL, "확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        String packageName = getPackageName();
                        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);

                        if (pm.isIgnoringBatteryOptimizations(packageName)) {
                            //
                            //intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                        }
                        else {
                            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                            intent.setData(Uri.parse("package:" + packageName));
                            startActivity(intent);
                        }
                    }
                });
                ad.show();
            }

        }

        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //서버에 로그인 요청 보내기
                ContentValues values = new ContentValues();
                values.put("id",t_id.getText().toString());
                values.put("pw",t_pw.getText().toString());
                pbar.setVisibility(View.VISIBLE);
                b_login.setVisibility(View.INVISIBLE);
                logintask nt = new logintask(requrl,values,pbar,true);
                nt.execute();
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
        sped.putString(pref_id, id); // 아이디 Preference에 저장
        sped.putString(pref_pw, pw); // 비번 Preference에 저장
        sped.apply();

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
    public static class staticlogintask extends AsyncTask<Void,Void,JSONObject>{
        String url;
        ContentValues values;
        View pbar;
        Context context;
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        boolean allsw;
        int allbt;
        public staticlogintask(String url, ContentValues values, View pbar, Context context,ArrayList<String> name, ArrayList<String> time,boolean allsw,int allbt){
            this.url=url;
            this.values=values;
            this.pbar=pbar;
            this.context = context;
            this.name = name;
            this.time = time;
            this.allsw = allsw;
            this.allbt = allbt;
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
                try {
                    JParser jp = new JParser();
                    jp.boardinfoParse(jsobj.toString(),name,time);
                } catch (ParseException e) {
                    Toast.makeText(context,"MySNU 정보 오류입니다.\n다시 로그인 해 주세요.",Toast.LENGTH_LONG).show();
                    return;
                }
                catch (RuntimeException e){
                    Toast.makeText(context,"MySNU 정보가 없습니다.\n아이디 혹은 비밀번호를 확인해주세요.",Toast.LENGTH_LONG).show();
                    return;
                }

                BoardSetting.DelDB(context);
                for(int i=0;i<name.size();i++){
                    BoardSetting.Add2DB(context,name.get(i),time.get(i),"1:30",true,allsw,allbt);
                }
                Toast.makeText(context,"시간표가 초기화되었습니다.",Toast.LENGTH_LONG).show();
                return;
            }
            else{
                Toast.makeText(context,"로그인 실패.\n아이디와 비밀번호을 확인해주세요.",Toast.LENGTH_LONG).show();
                return;
            }
        }
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
