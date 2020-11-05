package app.zingo.mysolite.utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import app.zingo.mysolite.R;

public class ProgressBarUtil {
    private ProgressDialog progressBar;
    private Context context;
    public ProgressBarUtil(Context context){
        this.context = context;
        if(progressBar==null) {
            progressBar = new ProgressDialog(context);
        }
    }
    public void showProgress(String s){
        if(progressBar!=null) {
            progressBar.setCancelable(true);
            progressBar.setProgressStyle( R.style.TextAppearance_AppCompat);
            progressBar.setMessage(s);
            if(!(( Activity ) context).isFinishing()) {
                progressBar.show();
            }
        }
    }
    public void hideProgress(){
        if(progressBar!=null){
            if(!((Activity) context).isFinishing()) {
                progressBar.dismiss();
            }
        }
    }
}
