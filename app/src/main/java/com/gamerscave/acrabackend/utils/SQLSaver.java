package com.gamerscave.acrabackend.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import static android.R.id.list;

public class SQLSaver {
    private static final String DATABASE_NAME = "MainDB.db";
    private static final String TABLE_NAME = "errors";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TIMES = "times";
    private static final String COLUMN_HASH = "hash";
    private static final String COLUMN_STACKTRACE = "stacktrace";
    private static final String COLUMN_DEVICES = "devices";
    private static final String COLUMN_LATEST_DATE = "lastreport";
    private static final String COLUMN_APP_NAME = "name";
    private static final String COLUMN_APP_VERSION = "appversion";
    private static final String COLUMN_ANDROID_V = "androidversion";
    Context c;
    SQLiteDatabase db;
    public SQLSaver(Context c) {
        db = c.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        db.execSQL(
                getCreateDatabaseQuery(TABLE_NAME)
        );
        this.c = c;
    }




    ///////////////
    ///////////////
    ///////////////

    public String createDeleteQuery(int id){
        String retval;
        retval = "D";
        return retval;
    }

    public String getCreateDatabaseQuery(@NonNull String TABLE_NAME){
        String retval = "CREATE TABLE IF NOT EXISTS '" + TABLE_NAME +
                "' (" + COLUMN_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIMES       + " LONG, " +
                COLUMN_HASH        + " TEXT, " +
                COLUMN_DEVICES     + " TEXT, " +
                COLUMN_STACKTRACE  + " TEXT, " +
                COLUMN_LATEST_DATE + " TEXT, " +
                COLUMN_APP_NAME    + " TEXT, " +
                COLUMN_APP_VERSION + " TEXT, " +
                COLUMN_ANDROID_V   + " TEXT)";
        return retval;
    }

    public HashMap<String, String> createAndInsertError(String stack, String hash, String devices,
                                    String lastreported, String appversion, String app, String android, String latest, Context c){
        ContentValues values = new ContentValues();
        stack = stack.replace("'", "");
        String sql = "INSERT INTO " + TABLE_NAME + " (" +
                COLUMN_STACKTRACE + ", " + COLUMN_HASH + ", " + COLUMN_APP_VERSION + ", " +
                COLUMN_APP_NAME + ", " + COLUMN_ANDROID_V + ", " + COLUMN_DEVICES + ", " +
                COLUMN_LATEST_DATE + ")"+
                " VALUES ('" + stack + "', '" + hash + "', '" + appversion + "', '" + app + "', '" + android + "', '" + devices + "', '" + latest + "');";

        db.execSQL(sql);
        Cursor s = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE `hash`='" + hash + "'", null);
        HashMap<String, String> data = new HashMap<>();
        s.moveToFirst();
        data.put("id", Long.toString(s.getInt(s.getColumnIndex(COLUMN_ID))));
        data.put("stack", stack);
        data.put("hash", hash);
        data.put("devices", devices);
        data.put("lastreport", lastreported);
        data.put("appversion", appversion);
        data.put("app", app);
        data.put("timesrep", "1");
        data.put("android", android);
        return data;
    }

    public List<Error> getAllErrors(Context c){
        List<Error> retval = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Error error;
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                String stack = cursor.getString(cursor.getColumnIndex(COLUMN_STACKTRACE));
                String hash = cursor.getString(cursor.getColumnIndex(COLUMN_HASH));
                String devices = cursor.getString(cursor.getColumnIndex(COLUMN_DEVICES));
                String lastreport = cursor.getString(cursor.getColumnIndex(COLUMN_LATEST_DATE));
                String appversion = cursor.getString(cursor.getColumnIndex(COLUMN_APP_VERSION));
                String app = cursor.getString(cursor.getColumnIndex(COLUMN_APP_NAME));
                String android = cursor.getString(cursor.getColumnIndex(COLUMN_ANDROID_V));
                long times = cursor.getLong(cursor.getColumnIndex(COLUMN_TIMES));
                error = new Error(id, hash, devices, stack, lastreport, appversion, app, times, android, c);
                retval.add(error);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return retval;
    }

    public boolean doesRowExist(String hash){
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE `" + COLUMN_HASH + "`='" + hash + "'",null);
        return cursor.moveToFirst();
    }

}
