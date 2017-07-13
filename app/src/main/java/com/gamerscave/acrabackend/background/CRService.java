package com.gamerscave.acrabackend.background;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamerscave.acrabackend.R;
import com.gamerscave.acrabackend.Settings;
import com.gamerscave.acrabackend.Splash;
import com.gamerscave.acrabackend.utils.Saver;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This is the background service. It will connect to a special(no password) script
 * and will simply tell you if there are errors in the directory where the logs are stored.
 * There are some variables involved to prevent repeated notifications, and after an error is
 * found, the script will no longer connect to the internet. At that point, an error is found
 * and there is, as such, no longer a need to connect to the internet.
 */
public class CRService extends Service {
    public static String SCRIPTLINK = null;
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        new Settings(getApplicationContext());
        SCRIPTLINK = Settings.DOMAIN + "" + Settings.DIRECTORY + "" + Settings.LIST_FILES;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        tt.cancel();
        timer = null;
        Toast.makeText(this, "ACRA backend service destroyed. If the app was opened, it will restart again when you exit. If you forced stop, you will not be notified of new crashes", Toast.LENGTH_LONG).show();
    }
    boolean looper = false;
    Timer timer = null;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(tt, 0, getTime());
        }
        /**
         * @see(Settings#SHOW_PERSISTENT)
         *
         */
        if(Settings.SHOW_PERSISTENT) {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(CRService.this);
            builder.setContentTitle("ACRA got you covered!")
                    .setAutoCancel(true)
                    .setColor(getResources().getColor(R.color.main))//TODO fix deprecated call
                    .setContentText("The backend is running, and you will be notified of potential crashes.")
                    .setSmallIcon(R.mipmap.ic_launcher);

            PendingIntent pendingIntent = PendingIntent.getActivity(CRService.this,
                    NOTIFICATION_ID,
                    new Intent(CRService.this, Splash.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            startForeground(1, builder.build());
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    boolean crashed = false;
    public long getTime(){
        /**
         * GUIDE:
         *
         * Time is the amount of time (in minutes) between each time the service should check for
         * a new crash.
         */
        long time = 30;

        return (1000 * 60) * time;
    }

    TimerTask tt = new TimerTask() {
        @Override
        public void run() {
            if(!looper){
                //Call Looper.prepare before creating calls to the internet.
                //This only needs to be done once per thread, and is only allowed
                //to do once per thread(hence the boolean). The thread is recalled,
                //but is still once instance
                Looper.prepare();
                looper = true;
            }
            boolean pushed = Saver.loadBoolean("pushed", getApplicationContext());
            Log.e("DEBUG", "TICK");

            if(Settings.DOMAIN == null) new Settings(getApplicationContext());
            if(!pushed){
                StringRequest stringRequest = new StringRequest(Request.Method.POST, SCRIPTLINK,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    if (Boolean.parseBoolean(response)) {
                                        crashed = true;
                                        Saver.save("pushed", "true", getApplicationContext());
                                        final NotificationCompat.Builder builder = new NotificationCompat.Builder(CRService.this);
                                        builder.setContentTitle("A crash has happened")
                                                .setAutoCancel(true)
                                                .setColor(getResources().getColor(R.color.danger))
                                                .setContentText("Whoops! One of your apps have crashed! Open the app to see the stacktrace," +
                                                        " and start debugging!")
                                                .setSmallIcon(R.mipmap.ic_launcher);

                                        PendingIntent pendingIntent = PendingIntent.getActivity(CRService.this,
                                                NOTIFICATION_ID,
                                                new Intent(CRService.this, Splash.class),
                                                PendingIntent.FLAG_UPDATE_CURRENT);
                                        builder.setContentIntent(pendingIntent);

                                        final NotificationManager manager = (NotificationManager) CRService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                        manager.notify(2, builder.build());
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
        }
    };

}
