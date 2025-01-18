package com.musicslayer.cashmaster.util;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.app.App;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class FileUtil {
    public static File writeTempFile(String s) {
        // Returns a temp file with the String written to it.
        File file;
        try {
            String appTitleCamelCase = App.applicationContext.getString(R.string.app_title_camel_case);
            file = File.createTempFile(appTitleCamelCase + "_TextFile_", ".txt", new File(App.cacheDir));
            FileUtils.writeStringToFile(file, s, StandardCharsets.UTF_8);
        }
        catch(Exception e) {
            ThrowableUtil.processThrowable(e);
            file = null;
        }

        return file;
    }
}
