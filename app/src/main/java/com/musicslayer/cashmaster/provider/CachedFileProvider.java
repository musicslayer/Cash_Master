package com.musicslayer.cashmaster.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.app.App;

import java.io.File;
import java.io.FileNotFoundException;

public class CachedFileProvider extends ContentProvider {
    private UriMatcher uriMatcher;

    @Override
    public boolean onCreate() {
        // Match files with our provider authority with code 1.
        // Match files with a different provider authority with code 2.
        // Invalid/empty files will give the default value of -1.
        // (Matching will return the first match that applies.)
        Context context = getContext();
        if(context == null) {
            throw new IllegalStateException();
        }

        String appPackage = context.getString(R.string.app_package);

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(appPackage + ".provider", "*", 1);
        uriMatcher.addURI("*", "*", 2);

        return true;
    }

    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        // Check incoming Uri against the matcher
        switch (uriMatcher.match(uri)) {
            case 1:
                // File is valid and has correct authority.
                String fileLocation = new File(App.cacheDir) + File.separator + uri.getLastPathSegment();
                return ParcelFileDescriptor.open(new File(fileLocation), ParcelFileDescriptor.parseMode(mode));

            case 2:
                // File is valid but does not have the correct authority.
                throw new FileNotFoundException("Unauthorized uri: " + uri);

            default:
                // Completely unrecognised Uri (for example, if Uri is empty or has invalid characters).
                throw new FileNotFoundException("Invalid uri: " + uri);
        }
    }

    // Unused. Just have them be no-ops.
    @Override
    public int update(@NonNull Uri uri, ContentValues contentvalues, String s, String[] as) { return 0; }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] as) { return 0; }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentvalues) { return null; }

    @Override
    public String getType(@NonNull Uri uri) { return null; }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String s, String[] as1, String s1) { return null; }
}