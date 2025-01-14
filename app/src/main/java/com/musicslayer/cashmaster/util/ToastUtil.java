package com.musicslayer.cashmaster.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.musicslayer.cashmaster.app.App;

import java.util.HashMap;

public class ToastUtil {
    final static HashMap<String, Toast> toastMap = new HashMap<>();

    @SuppressLint({"ShowToast"})
    public static void initialize() {
        Context context = App.applicationContext;
        // Use a dummy value for the duration. When the toast is shown, we will set it according to the setting.
        int duration = Toast.LENGTH_SHORT;

        toastMap.put("line_item_exists", Toast.makeText(context, "A line item with this name and month already exists.", duration));
        toastMap.put("must_fill_inputs", Toast.makeText(context, "All red input fields must be filled with appropriate values.", duration));
    }

    private static int getToastDuration() {
        // There is no setting in this app; we just use "short".
        return Toast.LENGTH_SHORT;
    }

    public static void showToast(String key) {
        Toast toast = toastMap.get(key);
        if(toast != null) {
            toast.setDuration(getToastDuration());

            // Toasts must always be shown on the UI Thread.
            // Use Looper so that we do not need access to the activity.
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // At some point, Android changed toast behavior. getView being null is the only way to tell which behavior we will see.
                    if(toast.getView() == null) {
                        // New behavior - We cannot check if the toast is showing, but it is always OK to cancel and (re)show the toast.
                        toast.cancel();
                        toast.show();
                    }
                    else if(!toast.getView().isShown()) {
                        // Old behavior - We cannot cancel, so show the toast only if it isn't already showing.
                        toast.show();
                    }
                }
           });
        }
    }
}
