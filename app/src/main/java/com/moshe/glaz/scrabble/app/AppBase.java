package com.moshe.glaz.scrabble.app;

import android.app.Application;
import android.content.Context;

public class AppBase extends Application {
    //instance of the main app context
    private static Context instanceContext;
    public static Context getContext() {
        return instanceContext;
    }

    // is calling when the app is created
    @Override
    public void onCreate() {
        super.onCreate();
        instanceContext=getApplicationContext();
    }
}
