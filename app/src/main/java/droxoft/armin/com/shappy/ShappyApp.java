package droxoft.armin.com.shappy;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;


@ReportsCrashes(
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri = "http://185.22.184.15:5984/acra-shappy/_design/acra-storage/_update/report",
        formUriBasicAuthLogin = "shappy",
        formUriBasicAuthPassword = "caxobaxo88",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text// optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
       )

public class ShappyApp extends Application {
        protected void attachBaseContext(Context base) {
            super.attachBaseContext(base);
            ACRA.init(this);
        }
}
