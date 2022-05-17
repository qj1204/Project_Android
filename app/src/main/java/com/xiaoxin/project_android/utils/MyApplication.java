package com.xiaoxin.project_android.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * 获取全局Context
 */
@SuppressLint("StaticFieldLeak")
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

}
