package com.gamerscave.acrabackend;

import android.app.Application;
import android.os.Bundle;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(

        formUri = "https://gamers-cave-world.com/crash/report.php",//Non-password protected.

        customReportContent = { /* */ReportField.APP_VERSION_NAME, ReportField.PACKAGE_NAME,ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,ReportField.LOGCAT },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash


)
public class BackendApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);

    }

}
