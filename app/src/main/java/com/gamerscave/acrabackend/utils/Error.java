package com.gamerscave.acrabackend.utils;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.gamerscave.acrabackend.content.Content;

import java.util.HashMap;
import java.util.Locale;

import static android.R.id.input;
import static android.os.Build.VERSION_CODES.M;

public class Error {
    public boolean merged;
    public int ID;
    public String hash;
    public String devices;
    public String stacktrace;
    public String lastreported;
    public String appversion;
    public String app;
    public int timesrep;
    public String android;
    public String otherInfo;
    public Context c;
    /**
     * Loads the issue from the database to display
     * @param ID
     * @param hash
     * @param devices
     * @param stacktrace
     * @param lastreported
     * @param appversion
     * @param app
     */
    public Error(int ID, String hash, String devices,
                 String stacktrace, String lastreported, String appversion,
                 String app, int times, String android, String otherInfo, Context c) {
        this.ID = ID;
        this.hash = hash;
        this.devices = devices;
        this.stacktrace = stacktrace;
        this.lastreported = lastreported;
        this.appversion = appversion;
        this.app = app;
        this.timesrep = times;
        this.android = android;
        this.c = c;
        this.otherInfo = otherInfo;
    }

    /**
     * Creates a new error. It automatically generates ID, hash and times reported
     * @param devices
     * @param stacktrace
     * @param lastreported
     * @param appversion
     * @param app
     */
    public Error(Context c, String devices, String stacktrace,
                 String lastreported, String appversion, String app, String android,
                 String otherInfo){
        /*
                data.put("id", Long.toString(newRowId));
        data.put("stack", stack);
        data.put("hash", hash);
        data.put("devices", devices);
        data.put("lastreport", lastreported);
        data.put("appversion", appversion);
        data.put("app", app);
        data.put("timesrep", "1");
         */

        String hash = Utils.md5(stacktrace);
        Log.e("DEBUG", hash);
        SQLSaver sql = new SQLSaver(c);
        if(sql.doesRowExist(hash)){
            Error e = null;
            for(Content.Item i : Content.ITEMS){
                Error s = i.error;
                if(s.getHash().equals(hash)){
                    e = s;
                    Log.e("DEBUG", "HASH = HASH");
                }
            }

            if(e == null) throw new RuntimeException("This is an impossible scenario");
            Log.e("DEBUG", "Error hash: " + e.getHash());
            merged = true;
            sql.updateSQL(devices, stacktrace, Utils.getDate(), appversion, app, android, e);
        }else {
            HashMap<String, String> finished = sql.createAndInsertError(stacktrace, hash, devices, lastreported, appversion, app, android, Utils.getDate(), otherInfo, c);
            this.stacktrace = finished.get("stack");
            this.hash = finished.get("hash");
            this.devices = finished.get("devices");
            this.lastreported = finished.get("lastreport");
            this.appversion = finished.get("appversion");
            this.app = finished.get("app");
            this.timesrep = Integer.parseInt(finished.get("timesrep"));
            this.ID = Integer.parseInt(finished.get("id"));
            this.android = finished.get("android");
            this.otherInfo = finished.get("info");
            finished.clear();//clear the hashmap...
            finished = null;//and delete the reference. Save memory.
        }
        sql.onDestroy();
    }

    public String getStacktrace(){
        return stacktrace;
    }

    public String getDesc(){
        String result = null;
        String[] res = stacktrace.split("\n");
        for(String s : res){
            if(s.contains("Exception") && !s.contains("ACRA caught")){
                result = s;
                break;
            }
        }
        if(result == null){
            return "Name not found";
        }
        result = result.replaceAll("E/ACRA", "");
        result = result.split("Exception:")[0] + "Exception";
        result = result.replaceAll("Caused by:", "");
        result = result.replaceAll(" ", "");
        return result;
    }

    public String getTitle(){

        return "Error #" + ID + ":";

    }

    public long getId(){
        return ID;
    }

    public long getTimes(){
        return timesrep;
    }

    public String getDevices(){
        return devices;
    }


    public String getHash() {
        return hash;
    }

    public String getLastreported() {
        return lastreported;
    }

    public String getAppversion() {
        return appversion;
    }

    public String getApp() {
        return app;
    }

    public String getAndroid(){
        return android;
    }

    public String getContent(){

            String raw = "App: %s\n" +
                    "Version: %s\n" +
                    "Hash: %s\n" +
                    "Devices: %s\n" +
                    "Last report: %s\n" +
                    "Times reported: %s\n" +
                    "Android versions: %s\n\n" +
                    "%s\n" +
                    "Stacktrace:\n%s" //This time we want to get a new line before showing the content
                    ;
            String finished = String.format(Locale.ENGLISH, raw, getApp(), getAppversion(), getHash(),
                    getDevices(), getLastreported(), Long.toString(getTimes()), getAndroid(), otherInfo, getStacktrace());

            return finished;

    }

    public void addTime(){
        timesrep++;
    }


}
