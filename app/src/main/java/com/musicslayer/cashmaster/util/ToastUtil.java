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
// TODO Update these toasts
    @SuppressLint({"ShowToast"})
    public static void initialize() {
        Context context = App.applicationContext;
        // Use a dummy value for the duration. When the toast is shown, we will set it according to the setting.
        int duration = Toast.LENGTH_SHORT;

        toastMap.put("category_name_cannot_be_same", Toast.makeText(context, "The new category name must be different than the current category name.", duration));
        toastMap.put("category_name_used", Toast.makeText(context, "A category with this name already exists.", duration));
        toastMap.put("item_name_cannot_be_same", Toast.makeText(context, "The new item name must be different than the current item name.", duration));
        toastMap.put("item_name_used", Toast.makeText(context, "An item with this name already exists.", duration));

        toastMap.put("must_fill_inputs", Toast.makeText(context, "All red input fields must be filled with appropriate values.", duration));

        toastMap.put("current_category_cannot_be_deleted", Toast.makeText(context, "The current category cannot be deleted.", duration));
        toastMap.put("favorite_category_cannot_be_deleted", Toast.makeText(context, "The favorite category cannot be deleted.", duration));
        toastMap.put("only_category_cannot_be_deleted", Toast.makeText(context, "The only remaining category cannot be deleted.", duration));
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
