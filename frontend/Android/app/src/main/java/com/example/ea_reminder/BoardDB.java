package com.example.ea_reminder;

import android.provider.BaseColumns;

public final class BoardDB {
    public static final class CreateDB implements BaseColumns {
        public static final String CLASSNAME = "[classname]";
        public static final String START = "[start]";
        public static final String LENGTH = "[length]";
        public static final String EAON = "[eaon]";
        public static final String ALARM = "[alarm]";
        public static final String TIMER = "[timer]";
        public static final String TABLENAME = "boardtable";

        public static final String CLEARDB = "DELETE FROM "+TABLENAME;
        //TODO: CREATE 문의 LENGTH도 not null 추가
        public static final String CREATE = "create table if not exists " + TABLENAME
                + "(" + _ID + " integer primary key autoincrement, "
                + CLASSNAME + " text not null , "
                + START + " text not null , "
                + LENGTH + " text , "
                + EAON + " bool not null , "
                + ALARM + " bool , "
                + TIMER + " int);";
    }
}

