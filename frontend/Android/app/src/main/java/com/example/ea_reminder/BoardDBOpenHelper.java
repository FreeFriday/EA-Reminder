package com.example.ea_reminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BoardDBOpenHelper {
    private static final String DATABASE_NAME = "InnerDatabase(SQLite).db";
    private static final int DATABASE_VERSION = 3;
    public static SQLiteDatabase db;

    private DBHelper dbhelper;
    private Context cntxt;

    public BoardDBOpenHelper(Context context){
        cntxt = context;
    }

    public BoardDBOpenHelper open() throws SQLException{
        dbhelper = new DBHelper(cntxt,DATABASE_NAME,null, DATABASE_VERSION);
        db = dbhelper.getWritableDatabase();
        return this;
    }

    public void create(){
        dbhelper.onCreate(db);
    }

    public void close(){
        db.close();
    }

    void putinvalue(ContentValues values, String classname, String start, String length, boolean eaon , boolean alarm, int timer){
        values.put(BoardDB.CreateDB.CLASSNAME, classname);
        values.put(BoardDB.CreateDB.START, start);
        values.put(BoardDB.CreateDB.LENGTH, length);
        values.put(BoardDB.CreateDB.EAON, eaon);
        values.put(BoardDB.CreateDB.ALARM, alarm);
        values.put(BoardDB.CreateDB.TIMER, timer);
    }

    public long insertcolumn(String classname, String start, String length, boolean eaon, boolean alarm, int timer){
        ContentValues values = new ContentValues();
        putinvalue(values,classname,start,length,eaon,alarm,timer);
        return db.insert(BoardDB.CreateDB.TABLENAME,null,values);
    }

    public Cursor selectcol(){
        return db.query(BoardDB.CreateDB.TABLENAME,null,null,null,null,null,null);
    }

    public int updatecol(long id, String classname, String start, String length, boolean eaon, boolean alarm, int timer){
        ContentValues values = new ContentValues();
        putinvalue(values,classname,start,length,eaon,alarm,timer);
        return db.update(BoardDB.CreateDB.TABLENAME,values,"_id="+id,null);
    }
    public void DelDB(){
        db.execSQL(BoardDB.CreateDB.CLEARDB);
        db.close();
    }

    public int deletecol(long id){
        return db.delete(BoardDB.CreateDB.TABLENAME,"_id="+id,null);
    }

    private class DBHelper extends SQLiteOpenHelper{

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context,name,factory,version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(BoardDB.CreateDB.CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("drop table if exists "+BoardDB.CreateDB.TABLENAME);
            onCreate(sqLiteDatabase);
        }
    }
}
