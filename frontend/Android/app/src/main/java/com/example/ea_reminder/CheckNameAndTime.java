package com.example.ea_reminder;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class CheckNameAndTime {
    public static final int UNDEF = -1;
    public static final int MON = 0;
    public static final int TUE = 1;
    public static final int WED = 2;
    public static final int THU = 3;
    public static final int FRI = 4;
    public static final int SAT = 5;
    public static final int SUN = 6;
    public static boolean CheckNameAndTime(String tempname, String temptime, ArrayList<Integer> retdates , ArrayList<Integer> rettimes, Context context){
        if(tempname.equals("")){//강좌 이름 비었는지 확인
            Toast.makeText(context,"강좌 이름을 설정해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            if(temptime.equals("")){//강좌 시간 비었는지 확인
                Toast.makeText(context, "강좌 시간을 설정해주세요", Toast.LENGTH_SHORT).show();
                return false;
            }
            else{
                StringTokenizer st = new StringTokenizer(temptime,",");//,로 토큰화
                ArrayList<String> strings = new ArrayList<String>();
                for(int i=0;st.hasMoreTokens();i++){
                    strings.add(st.nextToken());
                }

                ArrayList<String> dates = new ArrayList<String>();
                ArrayList<String> times = new ArrayList<String>();
                boolean passedtoken = true;
                for(int i=0;i<strings.size();i++){
                    StringTokenizer st2 = new StringTokenizer(strings.get(i)," ");
                    String tempdates = "";
                    String temptimes = "";
                    //한 토큰 내에 공백 구분이 한번만 있는지 확인
                    if(st2.hasMoreTokens()){
                        tempdates = st2.nextToken();
                        if(st2.hasMoreTokens()){
                            temptimes = st2.nextToken();
                            if(st2.hasMoreTokens()){
                                //공백 2번 이상인 경우
                                //HH시 반 형식 시도
                                String  temptimes2= st2.nextToken();
                                if(temptimes.contains("시")){
                                    int pos = temptimes.indexOf("시");
                                    String temph = temptimes.substring(0,pos);
                                    if(temptimes2.contains("분")){
                                        int pos2 = temptimes2.indexOf("분");
                                        String temph2 = temptimes2.substring(0,pos2);
                                        temptimes = temph+":"+temph2;
                                    }
                                    else{
                                        if(temptimes2.equals("반")){
                                            temptimes = temph+":30";
                                        }
                                        else{
                                            //모르는 형식일 경우
                                            passedtoken=false;
                                        }
                                    }

                                }
                                else{
                                    //다 실패 시
                                    passedtoken=false;
                                }
                            }
                            else{//공백 수가 맞다면

                            }
                        }
                        else{
                            //공백 구분 부족
                            passedtoken=false;
                        }
                    }
                    else{
                        passedtoken=false;
                    }
                    if(!passedtoken){
                        makeBeggingToast(context);
                        return false;
                    }
                    else{
                        int tempdatesint = UNDEF;
                        //요일 형식에 맞는지 확인
                        switch (tempdates){
                            case "월":
                                tempdatesint = MON;
                                break;
                            case "월요일":
                                tempdatesint = MON;
                                break;
                            case "MON":
                                tempdatesint = MON;
                                break;
                            case "mon":
                                tempdatesint = MON;
                                break;
                            case "Mon":
                                tempdatesint = MON;
                                break;
                            case "Monday":
                                tempdatesint = MON;
                                break;
                            case "monday":
                                tempdatesint = MON;
                                break;
                            case "화":
                                tempdatesint = TUE;
                                break;
                            case "화요일":
                                tempdatesint = TUE;
                                break;
                            case "TUE":
                                tempdatesint = TUE;
                                break;
                            case "tue":
                                tempdatesint = TUE;
                                break;
                            case "Tue":
                                tempdatesint = TUE;
                                break;
                            case "Tuesday":
                                tempdatesint = TUE;
                                break;
                            case "tuesday":
                                tempdatesint = TUE;
                                break;
                            case "수":
                                tempdatesint = WED;
                                break;
                            case "수요일":
                                tempdatesint = WED;
                                break;
                            case "WED":
                                tempdatesint = WED;
                                break;
                            case "wed":
                                tempdatesint = WED;
                                break;
                            case "Wed":
                                tempdatesint = WED;
                                break;
                            case "Wednesday":
                                tempdatesint = WED;
                                break;
                            case "wednesday":
                                tempdatesint = WED;
                                break;
                            case "목":
                                tempdatesint = THU;
                                break;
                            case "목요일":
                                tempdatesint = THU;
                                break;
                            case "THU":
                                tempdatesint = THU;
                                break;
                            case "thu":
                                tempdatesint = THU;
                                break;
                            case "Thu":
                                tempdatesint = THU;
                                break;
                            case "Thursday":
                                tempdatesint = THU;
                                break;
                            case "thursday":
                                tempdatesint = THU;
                                break;
                            case "금":
                                tempdatesint = FRI;
                                break;
                            case "금요일":
                                tempdatesint = FRI;
                                break;
                            case "FRI":
                                tempdatesint = FRI;
                                break;
                            case "fri":
                                tempdatesint = FRI;
                                break;
                            case "Fri":
                                tempdatesint = FRI;
                                break;
                            case "Friday":
                                tempdatesint = FRI;
                                break;
                            case "friday":
                                tempdatesint = FRI;
                                break;
                            case "토":
                                tempdatesint = SAT;
                                break;
                            case "토요일":
                                tempdatesint = SAT;
                                break;
                            case "SAT":
                                tempdatesint = SAT;
                                break;
                            case "sat":
                                tempdatesint = SAT;
                                break;
                            case "Sat":
                                tempdatesint = SAT;
                                break;
                            case "Saturday":
                                tempdatesint = SAT;
                                break;
                            case "saturday":
                                tempdatesint = SAT;
                                break;
                            case "일":
                                tempdatesint = SUN;
                                break;
                            case "일요일":
                                tempdatesint = SUN;
                                break;
                            case "SUN":
                                tempdatesint = SUN;
                                break;
                            case "sun":
                                tempdatesint = SUN;
                                break;
                            case "Sun":
                                tempdatesint = SUN;
                                break;
                            case "Sunday":
                                tempdatesint = SUN;
                                break;
                            case "sunday":
                                tempdatesint = SUN;
                                break;
                        }
                        if(tempdatesint==UNDEF){
                            passedtoken=false;
                        }
                        else{
                            //요일 성공 시 시간 형식 확인
                            //HH시MM분 형식이면 HH:MM으로 변경
                            if(temptimes.contains("시")&&temptimes.contains("분")){
                                temptimes = temptimes.substring(0,temptimes.indexOf("시"))+":"+temptimes.substring(temptimes.indexOf("시")+1,temptimes.indexOf("분"));
                                System.out.println(temptimes);
                            }
                            if(temptimes.charAt(temptimes.length()-1)=='시'){
                                temptimes = temptimes.substring(0,temptimes.indexOf("시"))+":00";
                            }
                            if(temptimes.charAt((temptimes.length()-2)>=0?temptimes.length()-2:0)=='시'&&temptimes.charAt((temptimes.length()-1)>=0?temptimes.length()-1:0)=='반'){
                                temptimes = temptimes = temptimes.substring(0,temptimes.indexOf("시"))+":30";
                            }
                            StringTokenizer st3 = new StringTokenizer(temptimes,":");
                            //시:분 형식인지 확인
                            int temphour=0;
                            int tempmin = 0;
                            if(st3.hasMoreTokens()){
                                try{
                                    temphour = Integer.parseInt(st3.nextToken());
                                }
                                catch(NumberFormatException e){
                                    passedtoken=false;
                                }
                                if(passedtoken){
                                    if(st3.hasMoreTokens()){
                                        try{
                                            tempmin = Integer.parseInt(st3.nextToken());
                                        }
                                        catch(NumberFormatException e){
                                            passedtoken = false;
                                        }
                                        if(passedtoken){
                                            if(st3.hasMoreTokens()){
                                                passedtoken = false;
                                            }
                                            else{
                                                //시:분 형식 맞음
                                                if(temphour>=0&&temphour<24){
                                                    if(tempmin>=0&&tempmin<60){
                                                        //최종 성공
                                                        retdates.add(tempdatesint);
                                                        rettimes.add(temphour*100+tempmin);
                                                    }
                                                    else{
                                                        passedtoken=false;
                                                    }
                                                }
                                                else{
                                                    passedtoken=false;
                                                }
                                            }
                                        }
                                    }
                                    else{
                                        passedtoken=false;
                                    }
                                }
                            }
                            else{
                                passedtoken = false;
                            }
                        }
                    }
                    if(!passedtoken){
                        makeBeggingToast(context);
                        return false;
                    }
                }

            }
        }
        return true;
    }
    public static void makeBeggingToast(Context context){
        Toast.makeText(context, "강좌 시간 형식을 맞춰주세요\n<요일> <시간>이며 각 요일은 ,로 구분합니다", Toast.LENGTH_LONG).show();
    }
}
