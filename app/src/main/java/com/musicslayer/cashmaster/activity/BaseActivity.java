package com.musicslayer.cashmaster.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.musicslayer.cashmaster.app.App;
import com.musicslayer.cashmaster.util.AppearanceUtil;

abstract public class BaseActivity extends AppCompatActivity {
    // Needed when the current activity is different than the activity captured in a closure.
    private static BaseActivity activity;

    abstract public void createLayout(Bundle savedInstanceState);

    // An optional method that updates the activity's layout. This can be called by dialogs started from the activity.
    public void updateLayout() {}

    public static void setCurrentActivity(BaseActivity activity) {
        BaseActivity.activity = activity;
    }

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
                Toast.makeText(this, "Cash Master needs to be restarted.", Toast.LENGTH_LONG).show();

                try {
                    // May throw Exceptions, but we don't care. We just need the app to eventually exit.
                    finish();
                }
                catch(Exception ignored) {
                }

                return;
            }
        }

        setCurrentActivity(this);
        AppearanceUtil.setAppearance(this);

        createLayout(savedInstanceState);
    }
}