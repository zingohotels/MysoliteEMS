package app.zingo.mysolite.AlarmManager;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import app.zingo.mysolite.Service.LocationForegroundService;
import app.zingo.mysolite.utils.PreferenceHandler;

@RequiresApi (api = Build.VERSION_CODES.LOLLIPOP)
public class InvokeAlarmService extends JobService {
    private AlarmManager alarmManager;

    @RequiresApi (api = Build.VERSION_CODES.O)
    @Override
    public boolean onStartJob( JobParameters params) {
        /*if(Build.VERSION.SDK_INT>25){
            Util.schedulerJob(getApplicationContext());
        }*/

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        triggerAlarmManager(calendar, "Login");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void triggerAlarmManager(Calendar firingCal, String string) {
        if(string!=null&&string.equalsIgnoreCase ("Login")) {
            Intent myIntent = new Intent (this, CheckOutAlarm.class);
            myIntent.putExtra ("type", string);
            PendingIntent pendingIntent = PendingIntent.getBroadcast (this, 0, myIntent, 0);
            alarmManager = (AlarmManager) this.getSystemService (ALARM_SERVICE);

            Calendar currentCal = Calendar.getInstance ();

            Calendar checkout = Calendar.getInstance ();
            checkout.set(Calendar.HOUR_OF_DAY, 20);
            checkout.set(Calendar.MINUTE, 00);
            checkout.set(Calendar.SECOND, 0);

            long intendedTime = firingCal.getTimeInMillis ();
            long currentTime = currentCal.getTimeInMillis ();
            long checkoutTime = checkout.getTimeInMillis ();

            if(currentTime>=checkoutTime){

                if(PreferenceHandler.getInstance ( getApplicationContext () ).isForeground ()){
                    Intent intent = new Intent(this, LocationForegroundService.class);
                    intent.setAction( LocationForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        this.startForegroundService(intent);
                    } else {
                        this.startService(intent);
                    }
                    System.out.println ("Stop Suree Alarm: ");
                }


            }
            else if(intendedTime<= currentTime) {
                System.out.println ("Start Suree Alarm: ");
                if( !PreferenceHandler.getInstance ( getApplicationContext () ).isForeground () ){
                    Intent intent = new Intent(this, LocationForegroundService.class);
                    intent.setAction( LocationForegroundService.ACTION_START_FOREGROUND_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        this.startForegroundService(intent);
                    } else {
                        this.startService(intent);
                    }
                }


                alarmManager.setRepeating ( AlarmManager.RTC, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
            } else {
                firingCal.add (Calendar.HOUR, 1);
                intendedTime = firingCal.getTimeInMillis ();
                alarmManager.setRepeating (AlarmManager.RTC, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
    }

    private boolean isServiceRunning(String serviceName){
        boolean serviceRunning = false;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List <ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
        Iterator <ActivityManager.RunningServiceInfo> i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = i.next();

            if(runningServiceInfo.service.getClassName().equals(serviceName)){
                serviceRunning = true;

                System.out.println ("True service" );

                if(runningServiceInfo.foreground) {
                    //service run in foreground
                    serviceRunning = true;
                    System.out.println ("True service" );
                }
            }
        }
        return serviceRunning;
    }
}