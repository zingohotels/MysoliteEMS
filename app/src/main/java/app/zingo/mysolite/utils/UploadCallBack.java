package app.zingo.mysolite.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface UploadCallBack {
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    void onProgressUpdate(int percentage);
}