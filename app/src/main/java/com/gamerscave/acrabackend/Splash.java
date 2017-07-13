package com.gamerscave.acrabackend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.gamerscave.acrabackend.content.Content;
import com.gamerscave.acrabackend.utils.SQLSaver;
import com.gamerscave.acrabackend.utils.Saver;

import java.util.Random;

/**
 * TODO: Create icons
 * TODO: Improve SplashScreen with image
 */
public class Splash extends Activity {
    /**
     * This variable defines the device that will send crash reports. Using this system allows
     * for testing without having to comment out different pieces before distributing them
     * over different devices.
     * Requires "debug" to be "true" for it to crash on said device.
     * This is used during testing of this backend, to see if everything works as expected,
     * and without having to deploy two different versions of the same app
     */
    public static final String TEST_DEVICE_CRASH = "SM-G925F";

    /**
     * Set to "true" if you deploy on two or more devices, and need to see if the reporting works.
     * This is used during the development of this app, to not have to create two different versions
     * here at the creation of the app.
     *
     * Setting this to true will crash on one of the devices(see TEST_DEVICE_CRASH) on boot. This will
     * (if configured) send the crash to you for you to view on one of your devices.
     *
     * You can, of course, keep this on false and use the crash button on the main screen. This is just
     * a shortcut implemented to speed up the process (and reduce taps)
     */
    public static final boolean DEBUG = false;

    public static boolean CREATING_SETTINGS = false;
    @Override
    public void onCreate(Bundle sis){
        super.onCreate(sis);
        final String mod = android.os.Build.MODEL.toLowerCase();
        if(!TEST_DEVICE_CRASH.toLowerCase().contains(mod) || !DEBUG) {
            new Settings(this);
            if(!CREATING_SETTINGS) {
                SQLSaver sql = new SQLSaver(this);
                sql.onDestroy();
                new Content(this);
            }

        }
        if(!CREATING_SETTINGS) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    //We crash inside the handler because of ACRA issue #574
                    //https://github.com/ACRA/acra/issues/574
                    if(TEST_DEVICE_CRASH.toLowerCase().contains(mod) && DEBUG){
                        throw new RuntimeException("This is a test crash 574");
                    }
                    //Reset the notification service
                    Saver.save("pushed", "false", Splash.this);
                    Intent i = new Intent(Splash.this, ErrorListActivity.class);
                    startActivity(i);
                    finish();//Delete this activity. It is not needed, and going back to it will cause issues
                }
            }, 2500);//Wait 2.5 seconds before activating. Give VolleyConnect time
        }else{
            finish();
        }
    }

}
