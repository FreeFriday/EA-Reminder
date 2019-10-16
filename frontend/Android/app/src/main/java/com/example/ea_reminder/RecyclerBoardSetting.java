package com.example.ea_reminder;

public class RecyclerBoardSetting {
    String name; //강좌 이름
    String time; //강좌 시작 시간
    String length;
    long id;
    boolean eaon;
    boolean alarm;
    int timer;

    RecyclerBoardSetting(long id,String name, String time,String length ,boolean eaon, boolean alarm, int timer){
        this.name = name;
        this.time = time;
        this.length=length;
        this.id=id;
        this.eaon = eaon;
        this.alarm = alarm;
        this.timer = timer;
    }
}
