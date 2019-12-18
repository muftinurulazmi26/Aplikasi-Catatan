package com.dev.mffa.mynote.Lib;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME ="mynote.db";
    public static final String TABLE_SQLITE = "myapi";
    public static final String API ="api";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE ="CREATE TABLE "+TABLE_SQLITE+" ("+
              API+" TEXT NOT NULL )";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SQLITE);
        onCreate(db);
    }

    public String getApi (){
        String api = null;
        SQLiteDatabase database = this.getWritableDatabase();
        String select = "SELECT * FROM "+TABLE_SQLITE;
        Cursor cursor = database.rawQuery(select,null);
        if (cursor.moveToFirst()){
            do {
                api = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        database.close();
        return api;
    }

    public void insert(String api){
        SQLiteDatabase database = this.getWritableDatabase();
        String queryValues = "INSERT INTO "+TABLE_SQLITE+ " (api) "+
        "VALUES ('" +api+"')";
        database.execSQL(queryValues);
        database.close();
    }
}
