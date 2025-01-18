package com.musicslayer.cashmaster.util;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;

import com.musicslayer.cashmaster.app.App;

public class ClipboardUtil {
    public static void exportText(String label, String text) {
        // Export to the clipboard, which shows different messages than "copy".
        ClipboardManager clipboard = (ClipboardManager) App.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE);

        // Check to see if size is too large.
        try {
            clipboard.setPrimaryClip(ClipData.newPlainText(label, text));
            ToastUtil.showToast("export_clipboard_success");
        }
        catch(RuntimeException e) {
            if(e.getCause() instanceof android.os.TransactionTooLargeException) {
                ToastUtil.showToast("export_clipboard_text_too_large");
            }
            else {
                // Something else went wrong but we don't know what.
                ToastUtil.showToast("export_clipboard_unknown_error");
            }
        }
    }

    public static CharSequence importText() {
        // Import from the clipboard, which shows different messages than "paste".
        ClipboardManager clipboard = (ClipboardManager) App.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE);

        if(!clipboard.hasPrimaryClip() || !clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            ToastUtil.showToast("import_clipboard_not_text");
            return null;
        }
        else if(clipboard.getPrimaryClip().getItemAt(0).getText() == null || "".contentEquals(clipboard.getPrimaryClip().getItemAt(0).getText())) {
            ToastUtil.showToast("import_clipboard_empty");
            return null;
        }
        else{
            // Don't show any message here, because we still don't know if the text is a valid format to be imported.
            return clipboard.getPrimaryClip().getItemAt(0).getText();
        }
    }
}
