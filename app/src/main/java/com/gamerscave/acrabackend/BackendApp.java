package com.gamerscave.acrabackend;

import android.app.Application;
import android.os.Bundle;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(

        formUri = "",//TODO add your link here
        /*Changes in the backend allows more than these fields.
        * You can now use whatever fields you need, and change this to whatever you need.*/
        customReportContent = {ReportField.APP_VERSION_NAME, ReportField.PACKAGE_NAME,//TODO modify this to your needs
                ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
                ReportField.LOGCAT, ReportField.BRAND,
                ReportField.REPORT_ID},
        mode = ReportingInteractionMode.TOAST,//TODO change if wanted
        resToastText = R.string.crash

)
public class BackendApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);

    }

}
