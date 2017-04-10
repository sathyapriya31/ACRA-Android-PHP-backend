package com.gamerscave.acrabackend;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
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

import java.io.File;

public class CRService extends Service {
    public static final String SCRIPTLINK = VolleyConnect.ROOT_DIR + "files.php";
    private static final int NOTIFICATION_ID = 1;

    private long mStartTime = 0L;
    private final Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask;
    private BackendApp app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = (BackendApp) getApplicationContext();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service finished.", Toast.LENGTH_SHORT).show();
        stopLog ();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (app.isRunning)
            return START_STICKY;


        mUpdateTimeTask = new Runnable() {
            public void run() {
                long millis = SystemClock.uptimeMillis() - mStartTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds     = seconds % 60;
                boolean pushed = Saver.loadBoolean("pushed", getApplicationContext());
                if(!pushed){
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, SCRIPTLINK,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try{
                                        if (Boolean.parseBoolean(response)) {
                                            final NotificationCompat.Builder builder = new NotificationCompat.Builder(CRService.this);
                                            builder.setContentTitle("A new error has been recorded by ACRA.")
                                                    .setAutoCancel(true)
                                                    .setColor(getResources().getColor(R.color.main))
                                                    .setContentText("A new error has been recorded by ACRA. Please open the app to see the error," +
                                                            "and get a new notification on the next crash.")
                                                    .setSmallIcon(R.mipmap.ic_launcher);

                                            PendingIntent pendingIntent = PendingIntent.getActivity(CRService.this,
                                                    NOTIFICATION_ID,
                                                    new Intent(CRService.this, Splash.class),
                                                    PendingIntent.FLAG_UPDATE_CURRENT);
                                            builder.setContentIntent(pendingIntent);

                                            final NotificationManager manager = (NotificationManager) CRService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                            manager.notify(NOTIFICATION_ID, builder.build());
                                            Saver.save("pushed", "true", getApplicationContext());
                                        }
                                    }catch(Exception e) {
                                        //IGNORE
                                    }


                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("DEBUG", "Error: " + error);
                                }
                            });

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);

                }

                mHandler.postAtTime(this, mStartTime + (((minutes * 60) + seconds + 10) * 1000));
                mHandler.postDelayed (mUpdateTimeTask, 10000);
            }};
        mStartTime = SystemClock.uptimeMillis();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 100);

        app.isRunning(true);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void stopLog () {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }




}
