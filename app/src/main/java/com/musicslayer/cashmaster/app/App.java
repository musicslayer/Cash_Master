package com.musicslayer.cashmaster.app;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.musicslayer.cashmaster.util.ThrowableUtil;

public class App extends Application {
    public static boolean isAppInitialized = false;

    // Store these for use later when the context may not be available.
    public static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            // Needed for older Android versions.
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            applicationContext = this.getApplicationContext();
        }
        catch(Exception e) {
            // Try to proceed on in case something above is no longer working in a later Android version.
            ThrowableUtil.processThrowable(e);
        }
    }
}