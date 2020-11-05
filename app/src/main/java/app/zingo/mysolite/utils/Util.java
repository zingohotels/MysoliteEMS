package app.zingo.mysolite.utils;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import app.zingo.mysolite.AlarmManager.InvokeAlarmService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by ZingoHotels Tech on 26-09-2018.
 */

public class Util {
    private static Retrofit retrofit = null;
    public static String IMAGE_URL;

    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor ();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build();
        Gson gson = new GsonBuilder ()
                .setLenient()
                .create();

        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl( Constants.BASE_URL)
                    .client(client)
                    .addConverterFactory( ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory( RxJava2CallAdapterFactory.create())
                    .build();

        }
        return retrofit;
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @RequiresApi (api = Build.VERSION_CODES.O)
    public static void schedulerJob(Context context) {
        ComponentName serviceComponent = new ComponentName (context, InvokeAlarmService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0,serviceComponent);
        builder.setMinimumLatency(1*1000);    // wait at least
        builder.setOverrideDeadline(600*1000);  //delay time
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);  // require unmetered network
        builder.setRequiresCharging(false);  // we don't care if the device is charging or not
        builder.setRequiresDeviceIdle(true); // device should be idle
        System.out.println("(scheduler Job");

        JobScheduler jobScheduler = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            jobScheduler = context.getSystemService(JobScheduler.class);
        }
        jobScheduler.schedule(builder.build());
    }

}
