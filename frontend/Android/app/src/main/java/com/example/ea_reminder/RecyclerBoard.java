package com.example.ea_reminder;

public class RecyclerBoard {
    String name; //강좌 이름
    String time; //강좌 시작 시간
    boolean on; //알림 여부
    int timer; //0= 10분전, 1= 5분전, 2= 1분전
    long id;

    public static int MAXTIMER = 2;
    public static final int TIMER_10MIN = 0;
    public static final int TIMER_5MIN = 1;
    public static final int TIMER_1MIN = 2;




    RecyclerBoard(String name, String time, boolean on, int timer,long id){
        this.name = name;
        this.time = time;
        this.on = on;
        this.timer = timer;
        this.id = id;
    }

}
