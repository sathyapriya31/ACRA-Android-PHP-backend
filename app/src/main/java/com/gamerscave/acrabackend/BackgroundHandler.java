package com.gamerscave.acrabackend;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamerscave.acrabackend.utils.Saver;
import com.gamerscave.acrabackend.utils.VolleyConnect;

import java.util.Calendar;
import java.util.Date;

public class BackgroundHandler extends WakefulBroadcastReceiver {
    public static final String SCRIPTLINK = VolleyConnect.ROOT_DIR + "files.php";
    private static final String ACTION_START_NOTIFICATION_SERVICE = "ACTION_START_NOTIFICATION_SERVICE";
    private static final String ACTION_DELETE_NOTIFICATION = "ACTION_DELETE_NOTIFICATION";
    private static final double NOTIFICATIONS_INTERVAL_IN_QUARTERS = 1;

    public static void setupAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = getStartPendingIntent(context);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                getTriggerAt(new Date()),
                /*NOTIFICATIONS_INTERVAL_IN_QUARTERS * AlarmManager.INTERVAL_FIFTEEN_MINUTES*/60000,
                alarmIntent);
    }
    boolean hasFiles;
    Intent serviceIntent = null;
    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        serviceIntent = null;

            Log.i(getClass().getSimpleName(), "onReceive from alarm, starting notification service");
        boolean pushed = Saver.loadBoolean("pushed", context);
        if(!pushed){

            StringRequest stringRequest = new StringRequest(Request.Method.POST, SCRIPTLINK,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("DEBUG", "Response: " + response);
                            if(Boolean.parseBoolean(response)){
                                hasFiles = true;
                                serviceIntent = BackgroundService.createIntentStartNotificationService(context);
                                if (serviceIntent != null) {
                                    startWakefulService(context, serviceIntent);
                                }
                                Saver.save("pushed", "true", context);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("DEBUG", "Error: " + error);
                        }
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);

        }



        if (serviceIntent != null) {
            startWakefulService(context, serviceIntent);
        }
    }

    private static long getTriggerAt(Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        //calendar.add(Calendar.HOUR, NOTIFICATIONS_INTERVAL_IN_HOURS);
        return calendar.getTimeInMillis();
    }

    private static PendingIntent getStartPendingIntent(Context context) {
        Intent intent = new Intent(context, Splash.class);
        intent.setAction(ACTION_START_NOTIFICATION_SERVICE);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getDeleteIntent(Context context) {
        Intent intent = new Intent(context, Splash.class);
        intent.setAction(ACTION_DELETE_NOTIFICATION);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}