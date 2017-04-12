package com.gamerscave.acrabackend.background;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gamerscave.acrabackend.Settings;


public class Boot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        startServiceIfNotRunning(context, new Intent(context, CRService.class));
    }

    public void startServiceIfNotRunning(Context c, Intent i){
        new Settings(c);//Settings is guaranteeably not initialized at this time.
        if(!isMyServiceRunning(CRService.class, c) && Settings.RUN_BACKGROUND)
            c.startService(i);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context c) {
        ActivityManager manager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
