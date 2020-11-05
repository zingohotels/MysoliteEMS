package app.zingo.mysolite.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

public class AppUtil {
    public static boolean isEmpty(EditText editText){
        if(TextUtils.isEmpty(editText.getText().toString())){
            return true;
        }
        return false;
    }
    public static void showToastMsg(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
