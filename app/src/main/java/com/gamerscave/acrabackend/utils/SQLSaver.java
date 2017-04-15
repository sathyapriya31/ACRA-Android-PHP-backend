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

import com.gamerscave.acrabackend.content.Content;

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

    public String getCreateDatabaseQuery(@NonNull String TABLE_NAME){
        String retval = "CREATE TABLE IF NOT EXISTS '" + TABLE_NAME +
                "' (" + COLUMN_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIMES       + " INTEGER, " +
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
                COLUMN_LATEST_DATE + "," + COLUMN_TIMES +
                ")"+
                " VALUES ('" + stack + "', '" + hash + "', '" + appversion + "', '"
                + app + "', '" + android + "', '" + devices + "', '"
                + latest + "', '" + 1 + "');";

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
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String stack = cursor.getString(cursor.getColumnIndex(COLUMN_STACKTRACE));
                String hash = cursor.getString(cursor.getColumnIndex(COLUMN_HASH));
                String devices = cursor.getString(cursor.getColumnIndex(COLUMN_DEVICES));
                String lastreport = cursor.getString(cursor.getColumnIndex(COLUMN_LATEST_DATE));
                String appversion = cursor.getString(cursor.getColumnIndex(COLUMN_APP_VERSION));
                String app = cursor.getString(cursor.getColumnIndex(COLUMN_APP_NAME));
                String android = cursor.getString(cursor.getColumnIndex(COLUMN_ANDROID_V));
                int times = cursor.getInt(cursor.getColumnIndex(COLUMN_TIMES));
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

    public void onDestroy(){
        db.close();
    }

    public void updateSQL(String devices, String stacktrace,
                          String lastreported, String appversion, String app, String android, Error e){
        String hash = Utils.md5(stacktrace);
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE `" + COLUMN_HASH + "`='" + hash + "'",null);
        if(c.moveToFirst()){
            //First, set up the basics
            int id = c.getInt(c.getColumnIndex(COLUMN_ID));
            //Now we get the variables we want to change
            String devs = c.getString(c.getColumnIndex(COLUMN_DEVICES));
            String and = c.getString(c.getColumnIndex(COLUMN_ANDROID_V));
            String ver = c.getString(c.getColumnIndex(COLUMN_APP_VERSION));
            int times = c.getInt(c.getColumnIndex(COLUMN_TIMES));
            times++;
            //Here we amend any new devices, android versions or app versions
            //into the original strings
            if(!devs.contains(devices)){
                devs += ", " + devices;
            }
            if(!and.contains(android)){
                and += ", " + android;
            }
            if(!ver.contains(appversion)){
                ver += ", " + appversion;
            }
            //Now we need to update the error....
            e.android = and;
            e.devices = devs;
            Log.e("DEBUG", "TIMES" + e.timesrep + ", " + times);
            e.timesrep = times;
            Log.e("DEBUG", "TIMES" + e.timesrep + ", " + times);
            e.lastreported = lastreported;
            String sql = "UPDATE " + TABLE_NAME + " SET " + COLUMN_DEVICES + "='" + devs + "', " + COLUMN_ANDROID_V + "='" + and + "', " +
                    COLUMN_TIMES + "='" + times + "', " + COLUMN_LATEST_DATE + "='" + lastreported + "', " + COLUMN_APP_VERSION + "='" + ver + "' " + " WHERE " + COLUMN_ID + "='" + id + "'";
            db.execSQL(sql);
            c.close();

        }
    }

    public void wipe(){
        String sql = "DELETE FROM " + TABLE_NAME;
        Content.ITEMS.clear();
        Content.ITEM_MAP.clear();
        db.execSQL(sql);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        Toast.makeText(c,
                "Wipe complete. You may need to restart the app for the changes to become visible.",
                Toast.LENGTH_LONG).show();
    }

    public void delete(String hash){
        String SQL = "DELETE FROM " + TABLE_NAME + " WHERE `hash`='" + hash + "'";
        db.execSQL(SQL);
        for(Content.Item i : Content.ITEMS){
            if(i.error.getHash().equals(hash)){
                Content.ITEMS.remove(i);
                break;
            }
        }
        Content.recreateItemMap();//Once the items have been updated, we need to recreate the item map.
    }
}
