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

        // App
        toastMap.put("cannot_delete_only_year", Toast.makeText(context, "Cannot delete the only year.", duration));
        toastMap.put("line_item_exists", Toast.makeText(context, "A line item with this name and month already exists.", duration));
        toastMap.put("must_fill_inputs", Toast.makeText(context, "All red input fields must be filled with appropriate values.", duration));
        toastMap.put("year_exists", Toast.makeText(context, "This year already exists.", duration));

        // Clipboard
        toastMap.put("copy_clipboard_text_too_large", Toast.makeText(context, "Cannot copy. Text is too large to place on clipboard.", duration));
        toastMap.put("copy_clipboard_unknown_error", Toast.makeText(context, "Cannot copy. Cause is unknown.", duration));
        toastMap.put("copy_clipboard_success", Toast.makeText(context, "Text copied to clipboard.", duration));
        toastMap.put("paste_clipboard_empty", Toast.makeText(context, "Cannot paste. Clipboard is empty.", duration));
        toastMap.put("paste_clipboard_not_text", Toast.makeText(context, "Cannot paste. Clipboard does not contain text.", duration));
        toastMap.put("paste_clipboard_success", Toast.makeText(context, "Text pasted from clipboard.", duration));

        // Email
        toastMap.put("cannot_attach", Toast.makeText(context, "Could not attach all files to the email.", duration));
        toastMap.put("email", Toast.makeText(context, "Your device does not have an email application.", duration));

        // Import and Export
        toastMap.put("export_clipboard_text_too_large", Toast.makeText(context, "Could not export to clipboard. Text is too large to place on clipboard.", duration));
        toastMap.put("export_clipboard_unknown_error", Toast.makeText(context, "Could not export to clipboard. Cause is unknown.", duration));
        toastMap.put("export_clipboard_success", Toast.makeText(context, "Export to clipboard complete.", duration));
        toastMap.put("import_clipboard_empty", Toast.makeText(context, "Could not import from clipboard. Clipboard is empty.", duration));
        toastMap.put("import_clipboard_not_from_app", Toast.makeText(context, "Could not import from clipboard. Text was not exported from this app.", duration));
        toastMap.put("import_clipboard_not_text", Toast.makeText(context, "Could not import from clipboard. Clipboard does not contain text.", duration));
        toastMap.put("import_clipboard_success", Toast.makeText(context, "Import from clipboard complete.", duration));
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
