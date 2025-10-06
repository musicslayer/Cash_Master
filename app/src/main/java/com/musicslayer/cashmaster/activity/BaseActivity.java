package com.musicslayer.cashmaster.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.musicslayer.cashmaster.app.App;
import com.musicslayer.cashmaster.util.AppearanceUtil;

abstract public class BaseActivity extends AppCompatActivity {
    abstract public void createLayout(Bundle savedInstanceState);

    // An optional method that updates the activity's layout. This can be called by dialogs started from the activity.
    public void updateLayout() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // In some situations (like manually removing a permission), the app may be "reset" and left in a bad state.
        // We need to exit the app and tell the user to restart.
        if(!App.isAppInitialized) {
            if(this instanceof InitialActivity) {
                // InitialActivity is the entry point of the app and is allowed to initialize.
                ((InitialActivity)this).initialize();
            }
            else {
                // If we get here, we are uninitialized, but we are not at the entry point of the app. Something went wrong!

                // The Toast database is not initialized, so manually create this toast.
                Toast.makeText(this, "App needs to be restarted.", Toast.LENGTH_LONG).show();

                try {
                    // May throw Exceptions, but we don't care. We just need the app to eventually exit.
                    finish();
                }
                catch(Exception ignored) {
                }

                return;
            }
        }

        AppearanceUtil.applyAppearance(this);

        createLayout(savedInstanceState);
    }
}