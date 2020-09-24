package app.zingo.mysolite.AlarmManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import static androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService;

public class CheckOutAlarm extends BroadcastReceiver {

    private static final String TAG = "CheckOutAlarm";
    Intent activityIntent = null;


    @Override
    public void onReceive( Context context, Intent intent) {

        if (intent != null) {

            String extra = intent.getStringExtra ("type");
            System.out.println ("Suree CheckOutAlarm " + extra);
            ComponentName comp = new ComponentName (context.getPackageName (), AlarmNotificationService.class.getName ());
            startWakefulService (context, ( intent.setComponent (comp) ));
        }
    }
}