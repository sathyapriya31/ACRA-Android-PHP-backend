package com.gamerscave.acrabackend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.gamerscave.acrabackend.content.Content;
import com.gamerscave.acrabackend.utils.Saver;

import java.util.Random;

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
     */
    public static final boolean DEBUG = true;
    @Override
    public void onCreate(Bundle sis){
        super.onCreate(sis);
        final String mod = android.os.Build.MODEL.toUpperCase();

        if(!TEST_DEVICE_CRASH.contains(mod) || !DEBUG) {
            new Content(this);
        }
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                //We crash inside the handler because of ACRA issue #574
                //https://github.com/ACRA/acra/issues/574
                if(TEST_DEVICE_CRASH.contains(mod) && DEBUG){
                    Random r = new Random();
                    boolean randOrDef = r.nextBoolean();

                    throw new RuntimeException("This is a test crash");

                }
                //Reset the notification service

                Saver.save("pushed", "false", Splash.this);
                Intent i = new Intent(Splash.this, ErrorListActivity.class);
                startActivity(i);
                finish();

            }
        }, 2500);

    }
}