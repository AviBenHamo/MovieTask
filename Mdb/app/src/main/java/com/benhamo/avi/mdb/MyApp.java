package com.benhamo.avi.mdb;

import android.app.Application;

public class MyApp extends Application {
    public static final String TAG = "com.avi.imdbproject";

    private static MyApp sharedInstance;

    public static MyApp getInstance(){
        return sharedInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.sharedInstance = this;
    }
}
