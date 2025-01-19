package com.musicslayer.cashmaster.util;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.PersistableBundle;

import com.musicslayer.cashmaster.app.App;

public class ClipboardUtil {
    public static void exportText(String label, String text, boolean isSensitive) {
        ClipboardManager clipboard = (ClipboardManager) App.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE);

        // Check to see if size is too large.
        try {
            ClipData clipData = ClipData.newPlainText(label, text);

            if(isSensitive) {
                // Mark the clipboard content as sensitive so it is not visible in the preview.
                PersistableBundle extras = new PersistableBundle();
                extras.putBoolean("android.content.extra.IS_SENSITIVE", true);
                clipData.getDescription().setExtras(extras);
            }

            clipboard.setPrimaryClip(clipData);
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
        ClipboardManager clipboard = (ClipboardManager) App.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE);

        boolean hasTextMimeType = clipboard.hasPrimaryClip()
            && clipboard.getPrimaryClipDescription() != null
            && (clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                || clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML));

        if(!hasTextMimeType) {
            ToastUtil.showToast("import_clipboard_not_text");
            return null;
        }

        boolean hasNonemptyText = clipboard.getPrimaryClip() != null
            && clipboard.getPrimaryClip().getItemAt(0).getText() != null
            && !"".contentEquals(clipboard.getPrimaryClip().getItemAt(0).getText());

        if(!hasNonemptyText) {
            ToastUtil.showToast("import_clipboard_empty");
            return null;
        }

        // Don't show any message here, because we still don't know if the text is a valid format to be imported.
        return clipboard.getPrimaryClip().getItemAt(0).getText();
    }
}
