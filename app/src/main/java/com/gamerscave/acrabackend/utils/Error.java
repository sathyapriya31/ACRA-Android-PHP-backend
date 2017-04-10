package com.gamerscave.acrabackend.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import static android.R.id.input;

public class Error {
    private long ID;
    private String hash;
    private String devices;
    private String stacktrace;
    private String lastreported;
    private String appversion;
    private String app;
    private long timesrep;
    private String android;
    private Context c;
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
    public Error(long ID, String hash, String devices,
                 String stacktrace, String lastreported, String appversion,
                 String app, long times, String android, Context c) {
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
                 String lastreported, String appversion, String app, String android){
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

        SQLSaver sql = new SQLSaver(c);
        if(sql.doesRowExist(hash)){
            //TODO allow for increase of times
        }else {
            HashMap<String, String> finished = sql.createAndInsertError(stacktrace, hash, devices, lastreported, appversion, app, android, "21.21.21", c);
            this.stacktrace = finished.get("stack");
            this.hash = finished.get("hash");
            this.devices = finished.get("devices");
            this.lastreported = finished.get("lastreport");
            this.appversion = finished.get("appversion");
            this.app = finished.get("app");
            this.timesrep = Long.parseLong(finished.get("timesrep"));
            this.ID = Long.parseLong(finished.get("id"));
            this.android = finished.get("android");
            finished.clear();//clear the hashmap...
            finished = null;//and delete the reference. Save memory.
        }
    }

    public String getStacktrace(){
        return stacktrace;
    }

    public String getDesc(){
        String result = stacktrace.split("Exception")[0];
        Log.e("DEBUG", "DESC: " + result);
        return result;
    }

    public String getTitle(){
        return "Error #" + ID;
    }

    public long getId(){
        return ID;
    }
}
