package app.zingo.mysolite.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import java.util.Objects;

public class NetworkUtil {
    public static boolean checkInternetConnection(Context context) {
        boolean flag = false;

        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if ( Objects.requireNonNull ( cm ).getActiveNetworkInfo() != null && Objects.requireNonNull ( cm.getActiveNetworkInfo ( ) ).isConnected()) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return flag;
    }
}
