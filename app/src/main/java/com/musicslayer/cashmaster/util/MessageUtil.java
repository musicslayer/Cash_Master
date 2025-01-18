package com.musicslayer.cashmaster.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

import com.musicslayer.cashmaster.R;

import java.io.File;
import java.util.ArrayList;

public class MessageUtil {
    public static void sendEmail(Activity activity, String toText, String subjectText, String bodyText, ArrayList<File> fileArrayList) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{toText});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subjectText);
        emailIntent.putExtra(Intent.EXTRA_TEXT, bodyText);

        // Use this so that only email apps are allowed to handle our intent, not other apps that also handle files.
        emailIntent.setSelector(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")));

        // Attach files. Don't bother with the extra steps if there are no files.
        if(fileArrayList != null) {
            // ClipData is needed to maintain compatibility with older Android versions,
            // and so that temporary permissions can be granted to allow the other app to read the files.
            ClipData clipData = ClipData.newRawUri("", null);

            ArrayList<Uri> uriArrayList = new ArrayList<>();
            for(File file : fileArrayList) {
                String appPackage = activity.getString(R.string.app_package);
                Uri uri = Uri.parse("content://" + appPackage + ".provider/" + file.getName());
                clipData.addItem(new ClipData.Item(uri));
                uriArrayList.add(uri);
            }

            emailIntent.setClipData(clipData);
            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriArrayList);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        ComponentName emailApp = emailIntent.resolveActivity(activity.getPackageManager());
        ComponentName unsupportedAction = ComponentName.unflattenFromString("com.android.fallback/.Fallback");
        if(emailApp != null && !emailApp.equals(unsupportedAction)) {
            activity.startActivity(emailIntent);
        }
        else {
            ToastUtil.showToast("email");
        }
    }
}
