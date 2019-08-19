package ru.sergeiandreev.forwardingsms;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SendSMS";
    public static final String TABLE_CONTACTS = "contacts";

    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_RECIEVER = "reciever";
    public static final String KEY_CHECKER = "checker";


    public DBHelper(AddActivity context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    public DBHelper(MainActivity context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL( "create table " + TABLE_CONTACTS + "(" + KEY_ID
        + " integer primary key," + KEY_TITLE + " text," + KEY_SENDER + " text," + KEY_RECIEVER + " text," + KEY_CHECKER + " text" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists " + TABLE_CONTACTS);

        onCreate(db);

    }

 }
