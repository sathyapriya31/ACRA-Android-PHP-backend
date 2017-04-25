package com.gamerscave.acrabackend;

import android.app.Application;
import android.os.Bundle;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(

        formUri = "https://gamers-cave-world.com/crash/report.php",
        /*Due to limitations in the development process, these are the only fields that are handled in the backend(app-side)*/
        customReportContent = {ReportField.APP_VERSION_NAME, ReportField.PACKAGE_NAME,
                ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
                ReportField.LOGCAT, ReportField.BRAND,
                ReportField.REPORT_ID},
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
