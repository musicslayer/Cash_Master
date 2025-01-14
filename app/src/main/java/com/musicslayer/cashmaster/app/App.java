package com.musicslayer.cashmaster.app;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    public static boolean isAppInitialized = false;

    // Store for use later when the context may not be available.
    public static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this.getApplicationContext();
    }
}